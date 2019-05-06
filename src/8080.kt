import java.awt.*
import java.awt.image.BufferedImage
import java.awt.image.BufferedImage.TYPE_BYTE_BINARY
import java.io.File
import java.util.*
import javax.swing.*
import kotlin.system.exitProcess


@kotlin.ExperimentalUnsignedTypes
//const val x01:UByte = 1u
//const val x02:UByte = 2u
//const val x04:UByte = 4u
//const val x05:UByte = 5u
//const val x08:UByte = 8u
//const val x10:UByte = 16u
//const val xFF:UShort = 255u
//const val x80:UShort = 128u
//const val xFFb:UByte = 254u
//const val x80b:UByte = 128u
//const val xFF00:UInt = 65280u
//const val xFF00s:UShort = 65280u
//const val x00FF:UInt = 255u
//const val xFFFF:UInt = 65535u
//const val xFFFF0000:UInt = 4294901760u
var opcount = 0
var last1000index =0
var last1000:UIntArray = UIntArray(1000)
var last1000sp:UIntArray = UIntArray(1000)
var lastString = ""
val cycles8080 = intArrayOf(
	4, 10, 7, 5, 5, 5, 7, 4, 4, 10, 7, 5, 5, 5, 7, 4, //0x00..0x0f
	4, 10, 7, 5, 5, 5, 7, 4, 4, 10, 7, 5, 5, 5, 7, 4, //0x10..0x1f
	4, 10, 16, 5, 5, 5, 7, 4, 4, 10, 16, 5, 5, 5, 7, 4, //etc
	4, 10, 13, 5, 10, 10, 10, 4, 4, 10, 13, 5, 5, 5, 7, 4,

	5, 5, 5, 5, 5, 5, 7, 5, 5, 5, 5, 5, 5, 5, 7, 5, //0x40..0x4f
	5, 5, 5, 5, 5, 5, 7, 5, 5, 5, 5, 5, 5, 5, 7, 5,
	5, 5, 5, 5, 5, 5, 7, 5, 5, 5, 5, 5, 5, 5, 7, 5,
	7, 7, 7, 7, 7, 7, 7, 7, 5, 5, 5, 5, 5, 5, 7, 5,

	4, 4, 4, 4, 4, 4, 7, 4, 4, 4, 4, 4, 4, 4, 7, 4, //0x80..8x4f
	4, 4, 4, 4, 4, 4, 7, 4, 4, 4, 4, 4, 4, 4, 7, 4,
	4, 4, 4, 4, 4, 4, 7, 4, 4, 4, 4, 4, 4, 4, 7, 4,
	4, 4, 4, 4, 4, 4, 7, 4, 4, 4, 4, 4, 4, 4, 7, 4,

	11, 10, 10, 10, 17, 11, 7, 11, 11, 10, 10, 10, 10, 17, 7, 11, //0xc0..0xcf
	11, 10, 10, 10, 17, 11, 7, 11, 11, 10, 10, 10, 10, 17, 7, 11,
	11, 10, 10, 18, 17, 11, 7, 11, 11, 5, 10, 5, 17, 17, 7, 11,
	11, 10, 10, 4, 17, 11, 7, 11, 11, 5, 10, 4, 17, 17, 7, 11
        )


@kotlin.ExperimentalUnsignedTypes
class ConditionCodes(){
    var z:UInt   = 0u
    var s:UInt   = 0u
    var p:UInt   = 0u
    var cy:UInt  = 0u
    var ac:UInt  = 0u
    var pad:UInt = 0u

}
@kotlin.ExperimentalUnsignedTypes
class Register(var value:UByte)

@kotlin.ExperimentalUnsignedTypes
class Registers(){

    var a:UByte = 0u
    var b:UByte = 0u
    var c:UByte = 0u
    var d:UByte = 0u
    var e:UByte = 0u
    var h:UByte = 0u
    var l:UByte = 0u
    var sp:UInt = 0u
    var pc:UInt = 0u
    var memory:UByteArray = UByteArray(65536)       //memory for machine, needs to be an array, size should be 16 * 0x1000
    var cc = ConditionCodes()
    var int_enable:Int = 0

    var shiftL:UInt = 0u
    var shiftM:UInt = 0u
    var shift_offset:Int = 0
    var lastInterupt = time()

    fun SpaceInvadersIn(port:UByte){

        val temp = port.toUInt()
        when(temp){
            0u -> a = 1u
            1u -> a = 0u
            3u -> {
                val value =shiftM.shl(8).or(shiftL)
                a= ((value.shr(8-shift_offset)).and(0x00FFu)).toUByte()
            }
        }
    }

    fun SpaceInvadersOut(port:UByte){
        val temp = port.toUInt()
        when(temp){
            2u -> shift_offset = a.and(7.toUByte()).toInt()
            4u -> {
                shiftL = shiftM
                shiftM = a.toUInt()
            }
        }
    }

    fun setZero(value:UShort){
        //condense this
        if(value.and(0xFFu).compareTo(0u) == 0){
            cc.z = 1u
        }else{
            cc.z = 0u
        }
    }
    fun setZero(value:UByte){
        //condense this
        if(value.and(0xFFu).compareTo(0u) == 0){
            cc.z = 1u
        }else{
            cc.z = 0u
        }
    }

    fun setSign(value:UShort){
        if(value.and(0x80u).compareTo(0u) == 0){
            cc.s = 0u
        }else{
            cc.s = 1u
        }
    }
    fun setSign(value:UByte){
        if(value.and(0x80u).compareTo(0u) == 0){
            cc.s = 0u
        }else{
            cc.s = 1u
        }
    }

    fun setCarry(value:UShort){
        if(value > 0xFFu){
            cc.cy = 1u
        }else{
            cc.cy = 0u
        }
    }
    fun setCarry(value:UByte){
        if(value > 0xFFu){
            cc.cy = 1u
        }else{
            cc.cy = 0u
        }
    }
    fun setPC(){
//        println(String.format("%04x", memory[pc.toInt()+2].toInt().shl(8)))
//        println(String.format("%04x", memory[pc.toInt()+1].toInt()))
        var upperByte = (memory[pc.toInt()+2]).toUInt()
        val lowerByte = memory[pc.toInt()+1].toUInt()
        upperByte = upperByte.shl(8)
        val combined = upperByte.or(lowerByte)

        pc = combined
    }
    fun setSP(){
        var upperByte = (memory[pc.toInt()+2]).toUInt()
        val lowerByte = memory[pc.toInt()+1].toUInt()
        upperByte = upperByte.shl(8)
        val combined = upperByte.or(lowerByte)

        sp = combined
    }
    fun offsetDE():UShort{
        val offset = (d.toUInt().shl(8).or(e.toUInt())).toUShort()

        return offset
    }
    fun offsetHL():UShort{
        val offset = (h.toUInt().shl(8).or(l.toUInt())).toUShort()

        return offset
    }

    fun LogicFlagsA(){
        cc.cy = 0u
        cc.ac = 0u
        setZero(a)
        setSign(a)
        //TODO implement parity
    }
    fun ArithFlags(res:UShort){
        if(res > 0xffu){
            cc.cy = 1u
        }else{
            cc.cy = 0u
        }

        if((res.and(0xffu)).toInt() == 0){
            cc.z = 1u
        }else{
            cc.z = 0u
        }

        if(0x80u.toUShort() == res.and(0x80u)){
            cc.s = 0u
        }else{
            cc.s = 1u
        }

        //TODO implemet parity

    }
    fun Push(high:UByte, low:UByte){
        WriteMem(sp.toInt()-1, high)
        WriteMem(sp.toInt()-2, low)
        sp = sp-2u
    }
    fun WriteMem(address:Int, value:UByte){
//        for(i in 9182 until 9215){
//            println(String.format("value: %02x     location: %04x      PC: %04x",memory[i].toInt(), i, pc.toInt()))
//        }
        if (address < 0x2000){
            println("ERROR: attempting to write to ROM")

            return
        }
        if (address >= 0x4000){
            println("Writing ouside of RAM not allowed")

            return
        }
        if(address == 0x20c1){
            println("Writing there")
        }
        memory[address] = value


    }
    fun GenerateInterupt(interupt_num:Int){
        val high = (pc.and(0xFF00u).shr(8)).toUByte()
        val low = (pc.and(0x00FFu)).toUByte()
        Push(high,low)
        pc = (8*interupt_num).toUInt()
        int_enable = 0

    }
    fun time():Long{
        return System.currentTimeMillis()
    }
    fun readFromHL():UByte{
        val offset = h.toInt().shl(8).or(l.toInt())
        return memory[offset]
    }
    fun writeToHL(value: UByte){
        val offset = h.toInt().shl(8).or(l.toInt())

        WriteMem(offset, value)
    }
    fun flagsZSP(value: UByte){
        setZero(value)
        setSign(value)
        //TODO Implement parity
    }
}

@kotlin.ExperimentalUnsignedTypes
//class screen():JPanel(){
//
//    init{
//        border = BorderFactory.createLineBorder(Color.WHITE)
////        val textArea = TextArea()
////        textArea.setText("|")
////        val scroolPane = JScrollPane(textArea)
//
//        val frame = JFrame("Space Invaders")
//
//
//        frame.getContentPane().add(scroolPane,BorderLayout.CENTER)
//        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)
//        frame.setSize(Dimension(256,224))
//        frame.setLocationRelativeTo(null)
//        frame.setVisible(true)
//
//    }
//    override fun getPreferredSize():Dimension{
//        val dime = Dimension(224,256)
//        return dime
//    }
//
//
//}
fun arrayToBitmap(stateMemory:UByteArray):BufferedImage{
    val img = BufferedImage(224,256,TYPE_BYTE_BINARY)
    var i = 0
    var j = 0
    val offset = 0x2400
    var allClear = true
    for(q in 0x2800 until 0x38ff){
        if(stateMemory[q] != 0u.toUByte()){
            allClear = false
        }
    }
    if(allClear){
        println("image should be empty")
    }
    for(x in 0 until img.getWidth()){
        for(y in img.getHeight()-1 downTo 0){
            var pixel = getBit(stateMemory[i+offset].toInt(),j)
            if(pixel == 1) {
                img.setRGB(x, y, 0xffffff)
            }else{
                img.setRGB(x, y, 0)
            }
            if(j==7){
                //println(stateMemory[i+offset].toInt().toString(2))
                i++
                j=0
            }else{
                j++
            }
        }
    }

    return img
}
private fun getBit(byte:Int, position:Int):Int{

    return(byte.shr(position).and(1))

}
@kotlin.ExperimentalUnsignedTypes
fun main() {
    val screen = JFrame("Space Invaders")
    var display = JLabel(ImageIcon())
    screen.getContentPane().add(display,BorderLayout.CENTER)
    screen.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)
    screen.setSize(Dimension(224,256))
    screen.setLocationRelativeTo(null)
    screen.setVisible(true)

    val bytes:UByteArray = File("invaders").readBytes().toUByteArray()
    //disassemble(bytes)
    var emulate = Registers()
    for(i in 0 until bytes.size){
        emulate.memory[i] = bytes[i]
    }
    var lastTimer = 0L
    var nextInterupt = 0L
    var whichInterupt = 1
    var nextFrame = 0L

    while(emulate.pc.toInt() < bytes.size){
        var now = System.currentTimeMillis()
        if (lastTimer == 0L){
            lastTimer = now
            nextInterupt = lastTimer + 16
            whichInterupt = 1

        }

        if((emulate.int_enable ==1) && now > nextInterupt){
            if(whichInterupt == 1){
                emulate.GenerateInterupt(1)
                whichInterupt = 2
            }
            else{
                emulate.GenerateInterupt(2)
                whichInterupt = 1
            }
            nextInterupt = now + 8
        }
        if((now - nextFrame) > 16){
            nextFrame = now+16
            //screen.remove(display)
            display.icon = ImageIcon(arrayToBitmap(emulate.memory))
            //display = JLabel(ImageIcon(arrayToBitmap(emulate.memory)))
            display.repaint()
            screen.add(display)
            display.repaint()
            screen.revalidate()
            screen.repaint()
        }
        var sinceLast = now - lastTimer
        var cyclesToCatchUp = (2 * sinceLast).toInt()*1000
        var cycles = 0
        while(cyclesToCatchUp > cycles) {
            var opcode = String.format("%02x",emulate.memory[emulate.pc.toInt()].toByte()).toUpperCase()
            if (opcode == "DB") {
//            println(opcode + "  "+ String.format("%04x", emulate.pc.toInt()) )

                var port = emulate.memory[emulate.pc.toInt() + 1]
                emulate.SpaceInvadersIn(port)
                emulate.pc++
                emulate.pc++
                cycles +=3
            } else if (opcode == "D3") {
//            println(opcode + "  "+ String.format("%04x", emulate.pc.toInt()) )

                var port = emulate.memory[emulate.pc.toInt() + 1]
                emulate.SpaceInvadersOut(port)
                emulate.pc++
                emulate.pc++
                cycles+=3
            } else {
                cycles += Emulate8080Op(emulate)
            }

        }
        lastTimer = now
        //println("Leaving loop")
//        opcount++
//        if(opcount%25 == 0){
//            Thread.sleep(1)
//        }
        //println(opcount)
    }

}

@kotlin.ExperimentalUnsignedTypes
fun UnimplementedInstruction(state:Registers){
    println(String.format("Error Unimplemented instruction:\t%02x\n",state.memory[state.pc.toInt()].toByte()))
    for(i in 9182 until 9215){
        println(String.format("%02x     %04x",state.memory[i].toInt(), i))
    }
    println(String.format("SP: %04x", state.sp.toInt()))
    exitProcess(2)
}

fun PrintLast1000(){
    for (i in 0 until 100){
        print(String.format("%04d ", i*10))
        for(j in 0 until 10){
            var n = i*10 + j
            print(String.format("%04x  %04x   ", last1000[n].toInt(), last1000sp[n].toInt()))
            if(n == last1000index){
                print("**")
            }
            println()
        }
    }
}

@kotlin.ExperimentalUnsignedTypes
fun Emulate8080Op(state:Registers):Int {
    var opcode  =  state.memory[state.pc.toInt()].toInt()
    //println(opcode + "  " + String.format("%04x", state.pc.toInt()))
    when (opcode) {
        0x00 -> {
            state.pc++
        } // NOP
        0x01 -> {                                   //LXI   B,byte
            state.c = state.memory[state.pc.toInt() + 1]
            state.b = state.memory[state.pc.toInt() + 2]
            state.pc = state.pc + 2u
            state.pc++
        }
        0x02 -> {                                   //STAX  B
            state.pc++
            val offset = (state.b.toInt().shl(8)).or(state.c.toInt())
            //println(String.format("%04x", state.b.toInt()))
            state.WriteMem(offset, state.a)

        }
        0x03 -> {                                   //INX   B
            state.c++
            if (state.c.toInt() == 0) {
                state.b++
            }
            state.pc++
        }
        0x04 -> {                                   //INR   B
            state.b++
            state.flagsZSP(state.b)
            state.pc++
        }
        0x05 -> {                                   //DCR   B
            state.b--
            state.flagsZSP(state.b)
            state.pc++
        }
        0x06 -> {                                   //MVI   B,byte
            state.b = state.memory[state.pc.toInt() + 1]
            state.pc++
            state.pc++
        }
        0x07 -> {
            val res = state.a.toUInt()
            state.a =  ((res.and(0x80u).shr(7)).or(res.shl(1))).toUByte()
            if (1u == (res.and(1u))) {
                state.cc.cy = 1u
            } else {
                state.cc.cy = 0u
            }
            state.pc++
        }
        0x08 -> UnimplementedInstruction(state)
        0x09 -> {                                   //DAD   B
            val hl: UInt = (state.h.toUInt().shl(8).or(state.l.toUInt()))
            //println(String.format("h: %02x l: %02x", state.h.toInt(), state.l.toInt()))
            val bc: UInt = (state.b.toUInt().shl(8).or(state.c.toUInt()))
            //println(String.format("h: %02x l: %02x", state.b.toInt(), state.c.toInt()))
            val res = hl + bc
            state.h = (res.and(0xFF00u).shr(8)).toUByte()
            state.l = res.and(0x00FFu.toUInt()).toUByte()
            //println(String.format("h: %02x l: %02x", state.h.toInt(), state.l.toInt()))
            if (res > 0xFFFFu) {
                state.cc.cy = 1u
            } else {
                state.cc.cy = 0u
            }
            state.pc++
        }
        0x0A -> {                                   //LDAX B
            val offset = (state.b.toUInt().shl(8).or(state.c.toUInt()))
            state.a = state.memory[offset.toInt()]
            state.pc++
        }
        0x0B -> UnimplementedInstruction(state)
        0x0C -> {                                   //INR   C
            state.c++
            state.flagsZSP(state.c)
            state.pc++
        }
        0x0D -> {                                   //DCR   C

            state.c--
            state.flagsZSP(state.c)
            state.pc++
        }
        0x0E -> {                                   //MVI   C,byte

            state.c = state.memory[state.pc.toInt() + 1]
            state.pc++
            state.pc++
        }
        0x0F -> {                                   //RRC

            var x = state.a.toUInt()
            state.a = ((x.and(1u).shl(7)).or(x.shr(1))).toUByte()
            if (1u == (x.and(1u))) {
                state.cc.cy = 1u
            } else {
                state.cc.cy = 0u
            }
            state.pc++
//            val x = state.a.and(1u)
//            val temp = state.a.toUInt()
//            state.a = (temp.shr(1).and(x.toUInt().shr(7))).toUByte()
//            if(x.toInt() == 0){
//                state.cc.cy = 0u
//            }else{
//                state.cc.cy = 1u
//            }
        }

        0x10 -> UnimplementedInstruction(state)
        0x11 -> {
            state.e = state.memory[state.pc.toInt() + 1]
            state.d = state.memory[state.pc.toInt() + 2]
            state.pc = state.pc + 2u
            state.pc++
        }
        0x12 -> UnimplementedInstruction(state)
        0x13 -> {                                       //INX   D
            state.e++
            if (state.e.toInt() == 0) {
                state.d++
            }
            state.pc++
        }
        0x14 -> {                                       //INR   D
            state.d++
            state.flagsZSP(state.d)
            state.pc++
        }
        0x15 -> {                                       //DCR   D
            state.d--
            state.flagsZSP(state.d)
            state.pc++
        }
        0x16 -> {
            state.d = state.memory[state.pc.toInt()+1]
            state.pc+=2u
        }
        0x17-> {
            val x = state.a
            state.a = (state.cc.cy.or(x.toUInt().shl(1))).toUByte()
            if (0x80 == (x.and(0x80u)).toInt()) {
                state.cc.cy = 1u
            } else {
                state.cc.cy = 0u
            }
            state.pc++
        }
        0x18 -> UnimplementedInstruction(state)
        0x19 -> {                                   //DAD   D
            val hl: UInt = (state.h.toUInt().shl(8).or(state.l.toUInt()))
            val de: UInt = (state.d.toUInt().shl(8).or(state.e.toUInt()))
            val res = hl + de
            state.h = (res.and(0xFF00u).shr(8)).toUByte()
            state.l = res.and(0x00FFu.toUInt()).toUByte()
            if (res > 0xFFFFu) {
                state.cc.cy = 1u
            } else {
                state.cc.cy = 0u
            }
            state.pc++
        }
        0x1A -> {                               //LDAX	D
            val offset = (state.d.toInt().shl(8)).or(state.e.toInt())
            state.a = state.memory[offset]
            state.pc++
        }
        0x1B -> UnimplementedInstruction(state)
        0x1C -> UnimplementedInstruction(state)
        0x1D-> UnimplementedInstruction(state)
        0x1E -> UnimplementedInstruction(state)
        0x1F -> {                               //RAR
            val res = state.a
            state.a = ((state.cc.cy).shl(7).or(res.toUInt().shr(1))).toUByte()
            if(1u.toUByte() == res.and(1u)){
                state.cc.cy = 1u
            }else{
                state.cc.cy = 0u
            }
            state.pc++
        }

        0x20 -> UnimplementedInstruction(state)
        0x21 -> {
            state.l = state.memory[state.pc.toInt() + 1]
            state.h = state.memory[state.pc.toInt() + 2]
            state.pc = state.pc + 2u
            state.pc++
        }
        0x22 -> {
            val offset = (state.memory[state.pc.toInt()+1].toUInt()).or(state.memory[state.pc.toInt()+2].toUInt().shl(8))
            state.WriteMem(offset.toInt(), state.l)
            state.WriteMem(offset.toInt()+1,state.h)
            state.pc+=3u
        }
        0x23 -> {                           //INX   H
            state.l++
            if (state.l.toInt() == 0) {
                state.h++
            }
            state.pc++
        }
        0x24 -> UnimplementedInstruction(state)
        0x25 -> UnimplementedInstruction(state)
        0x26 -> {                                   //MVI   H,byte
            state.h = state.memory[state.pc.toInt() + 1]
            state.pc++
            state.pc++
        }
        0x27 -> {
            UnimplementedInstruction(state)
        }
        0x28 -> UnimplementedInstruction(state)
        0x29 -> {                                   //DAD   HL
            val hl: UInt = (state.h.toUInt().shl(8).or(state.l.toUInt()))
            val res: UInt = hl + hl
            state.h = (res.and(0xFF00u).shr(8)).toUByte()
            state.l = res.and(0x00FFu.toUInt()).toUByte()
            if (res > 0xFFFFu) {
                state.cc.cy = 1u
            } else {
                state.cc.cy = 0u
            }
            state.pc++

        }
        0x2A -> {
            val offset = (state.memory[state.pc.toInt()+1].toUInt()).or(state.memory[state.pc.toInt()+2].toUInt().shl(8))
            state.l = state.memory[offset.toInt()]
            state.h = state.memory[offset.toInt()+1]
            state.pc+=3u
        }
        0x2B -> {
            state.l--
            if(state.l == 0xffu.toUByte()){
                state.h--
            }
            state.pc++
        }
        0x2C -> UnimplementedInstruction(state)
        0x2D -> UnimplementedInstruction(state)
        0x2E -> {
            state.l = state.memory[state.pc.toInt()+1]
            state.pc+=2u
        }
        0x2F -> {                                   //CMA
            state.a = state.a.inv()
            state.pc++
        }

        0x30 -> UnimplementedInstruction(state)
        0x31 -> {                                   //LXI   SP
            state.sp = (state.memory[state.pc.toInt() + 2].toUInt().shl(8)).or(state.memory[state.pc.toInt() + 1].toUInt())

            state.pc++
            state.pc++
            state.pc++
        }
        0x32 -> {                                   //STA   byte
            val offset = (state.memory[state.pc.toInt() + 2].toInt()).shl(8)
                    .or(state.memory[state.pc.toInt() + 1].toInt())
            val temp1 = state.memory[state.pc.toInt() + 2]
            val temp2 = state.memory[state.pc.toInt() + 1]
            state.WriteMem(offset, state.a)
            state.pc = state.pc + 2u
            state.pc++
        }
        0x33 -> UnimplementedInstruction(state)
        0x34 -> {                                   //INR   (HL)
            val res = (state.readFromHL() +1u).toUByte()
            state.flagsZSP(res)
            state.writeToHL(res)
            state.pc++
        }
        0x35 -> {
            val res = (state.readFromHL() - 1u).toUByte()
            state.setZero(res)
            state.setSign(res)
            //TODO implemet parity
            state.writeToHL(res)
            state.pc++
        }
        0x36 -> {                                   //MVI   M,byte
//            val offset = state.h.toUInt().shl(8).or(state.l.toUInt())
//            state.memory[offset.toInt()] = state.memory[state.pc.toInt() + 1]
//            if(state.pc == 0x09d9u){
//                println(state.memory[state.pc.toInt()+1])
//                println(String.format("%02x%02x",state.h.toByte(),state.l.toByte()))
//            }

            state.writeToHL(state.memory[state.pc.toInt()+1])
            state.pc++
            state.pc++
        }
        0x37 -> {
            state.cc.cy = 1u
            state.pc++
        }
        0x38 -> UnimplementedInstruction(state)
        0x39 -> {                                   //DAD   SP
            val hl: UInt = state.h.toUInt().shl(8).or(state.l.toUInt())
            val res = hl + state.sp
            state.h = (res.and(0xFF00u)).shr(8).toUByte()
            state.l = res.and(0xFFu).toUByte()
            if (res > 0xFFFFu) {
                state.cc.cy = 1u
            } else {
                state.cc.cy = 0u
            }
            state.pc++
        }
        0x3A -> {                                   //LDA   byte
            val offset = (state.memory[state.pc.toInt() + 2].toUInt()).shl(8)
                    .or(state.memory[state.pc.toInt() + 1].toUInt())
            //print(String.format("\t%04x\n", offset.toInt()))
            //println("aaa" + state.memory[offset.toInt()])
            state.a = state.memory[offset.toInt()]
            state.pc = state.pc + 2u
            state.pc++
        }
        0x3B -> UnimplementedInstruction(state)
        0x3C -> {                                   //INR   A
            state.a++
            state.flagsZSP(state.a)
            state.pc++
        }
        0x3D -> {                                   //DCR   A
            state.a--
            state.flagsZSP(state.a)
            state.pc++
        }
        0x3E -> {                                   //MVI   A,byte
            state.a = state.memory[state.pc.toInt() + 1]
            state.pc++
            state.pc++

        }
        0x3F -> UnimplementedInstruction(state)

        0x40 -> UnimplementedInstruction(state)
        0x41 -> UnimplementedInstruction(state)
        0x42 -> UnimplementedInstruction(state)
        0x43 -> UnimplementedInstruction(state)
        0x44 -> UnimplementedInstruction(state)
        0x45 -> UnimplementedInstruction(state)
        0x46 -> {
            state.b = state.readFromHL()
            state.pc++
        }
        0x47 -> {
            state.b = state.a
            state.pc++
        }
        0x48 -> UnimplementedInstruction(state)
        0x49 -> UnimplementedInstruction(state)
        0x4A -> UnimplementedInstruction(state)
        0x4B -> UnimplementedInstruction(state)
        0x4C -> UnimplementedInstruction(state)
        0x4D -> UnimplementedInstruction(state)
        0x4E -> {
            state.c = state.readFromHL()
            state.pc++
        }
        0x4F -> {
            state.c = state.a
            state.pc++
        }

        0x50 -> UnimplementedInstruction(state)
        0x51 -> UnimplementedInstruction(state)
        0x52 -> UnimplementedInstruction(state)
        0x53 -> UnimplementedInstruction(state)
        0x54 -> UnimplementedInstruction(state)
        0x55 -> UnimplementedInstruction(state)
        0x56 -> {                                   //MOV   D,M
            state.d = state.memory[state.offsetHL().toInt()]
            state.pc++
        }
        0x57 -> {
            state.d = state.a
            state.pc++
        }
        0x58 -> UnimplementedInstruction(state)
        0x59 -> UnimplementedInstruction(state)
        0x5A -> UnimplementedInstruction(state)
        0x5B -> UnimplementedInstruction(state)
        0x5C -> UnimplementedInstruction(state)
        0x5D -> UnimplementedInstruction(state)
        0x5E -> {                                   //MOV   E,M
            state.e = state.memory[state.offsetHL().toInt()]
            state.pc++
        }
        0x5F -> {
            state.e = state.a
            state.pc++
        }

        0x60 -> UnimplementedInstruction(state)
        0x61 -> {                                  //MOV    H,C
            state.h = state.c
            state.pc++
        }
        0x62 -> UnimplementedInstruction(state)
        0x63 -> UnimplementedInstruction(state)
        0x64 -> UnimplementedInstruction(state)
        0x65 -> {
            state.h = state.l
            state.pc++
        }
        0x66 -> {                                   //MOV   H,M
            state.h = state.memory[state.offsetHL().toInt()]
            state.pc++
        }
        0x67 -> {
            state.h = state.a
            state.pc++
        }
        0x68 -> {                               //MOV   L,B
            state.l = state.b
            state.pc++
        }
        0x69 -> {                                   //
            state.l = state.c
            state.pc++
        }
        0x6A -> UnimplementedInstruction(state)
        0x6B -> UnimplementedInstruction(state)
        0x6C -> UnimplementedInstruction(state)
        0x6D -> UnimplementedInstruction(state)
        0x6E -> UnimplementedInstruction(state)
        0x6F -> {                                   //MOV   L,A
            state.l = state.a
            state.pc++
        }

        0x70 -> {                                   //MOV (HL), B
            state.writeToHL(state.b)
            state.pc++
        }
        0x71 -> UnimplementedInstruction(state)
        0x72 -> UnimplementedInstruction(state)
        0x73 -> UnimplementedInstruction(state)
        0x74 -> UnimplementedInstruction(state)
        0x75 -> UnimplementedInstruction(state)
        0x76 -> UnimplementedInstruction(state)
        0x77 -> {                                   //MOV   M,A
            //println(String.format("%02x%02x",state.h.toByte(),state.l.toByte()))
            state.writeToHL(state.a)
            state.pc++

        }
        0x78 -> {
            state.a = state.b
            state.pc++
        }
        0x79 -> {
            state.a = state.c
            state.pc++
        }
        0x7A -> {                                   //MOV   D,A
            state.a = state.d
            state.pc++
        }
        0x7B -> {                                   //MOV   E,A
            state.a = state.e
            state.pc++
        }
        0x7C -> {                                    //MOV   A,H
            state.a = state.h
            state.pc++
        }
        0x7D -> {                                   //
            state.a = state.l
            state.pc++
        }
        0x7E -> {                                   //MOV   A,M
            state.a = state.memory[state.offsetHL().toInt()]
            state.pc++
        }
        0x7F -> UnimplementedInstruction(state)

        0x80 -> {
            val res: UShort = (state.a.toUShort() + state.b.toUShort()).toUShort()
            state.ArithFlags(res)
            state.a = res.and(0xffu).toUByte()
            state.pc++
        }
        0x81 -> {
            val res: UShort = (state.a.toUShort() + state.c.toUShort()).toUShort()
            state.ArithFlags(res)
            state.a = res.and(0xffu).toUByte()
            state.pc++
        }
        0x82 -> {
            val res: UShort = (state.a.toUShort() + state.d.toUShort()).toUShort()
            state.ArithFlags(res)
            state.a = res.and(0xffu).toUByte()
            state.pc++
        }
        0x83 -> {
            val res: UShort = (state.a.toUShort() + state.e.toUShort()).toUShort()
            state.ArithFlags(res)
            state.a = res.and(0xffu).toUByte()
            state.pc++
        }
        0x84 -> {
            val res: UShort = (state.a.toUShort() + state.h.toUShort()).toUShort()
            state.ArithFlags(res)
            state.a = res.and(0xffu).toUByte()
            state.pc++
        }
        0x85 -> {
            val res: UShort = (state.a.toUShort() + state.l.toUShort()).toUShort()
            state.ArithFlags(res)
            state.a = res.and(0xffu).toUByte()
            state.pc++
        }
        0x86 -> {                                   //ADD   (HL)
            val res = (state.a + state.readFromHL()).toUShort()
            state.ArithFlags(res)
            state.a = res.toUByte()
            state.pc++
        }
        0x87 -> UnimplementedInstruction(state)
        0x88 -> UnimplementedInstruction(state)
        0x89 -> UnimplementedInstruction(state)
        0x8A -> UnimplementedInstruction(state)
        0x8B -> UnimplementedInstruction(state)
        0x8C -> UnimplementedInstruction(state)
        0x8D-> UnimplementedInstruction(state)
        0x8E -> UnimplementedInstruction(state)
        0x8F -> UnimplementedInstruction(state)

        0x90 -> UnimplementedInstruction(state)
        0x91 -> UnimplementedInstruction(state)
        0x92 -> UnimplementedInstruction(state)
        0x93 -> UnimplementedInstruction(state)
        0x94 -> UnimplementedInstruction(state)
        0x95 -> UnimplementedInstruction(state)
        0x96 -> UnimplementedInstruction(state)
        0x97 -> UnimplementedInstruction(state)
        0x98 -> UnimplementedInstruction(state)
        0x99 -> UnimplementedInstruction(state)
        0x9A -> UnimplementedInstruction(state)
        0x9B -> UnimplementedInstruction(state)
        0x9C -> UnimplementedInstruction(state)
        0x9D -> UnimplementedInstruction(state)
        0x9E -> UnimplementedInstruction(state)
        0x9F -> UnimplementedInstruction(state)

        0xA0 ->{
            state.a = state.a.and(state.b)
            state.LogicFlagsA()
            state.pc++
        }
        0xA1 -> UnimplementedInstruction(state)
        0xA2 -> UnimplementedInstruction(state)
        0xA3 -> UnimplementedInstruction(state)
        0xA4 -> UnimplementedInstruction(state)
        0xA5 -> UnimplementedInstruction(state)
        0xA6 -> {                                   //ANA   (HL)
            state.a = state.a.and(state.readFromHL())
            state.LogicFlagsA()
            state.pc++
        }
        0xA7 -> {                                   //ANA   A
            state.a = state.a.and(state.a)
            state.LogicFlagsA()
            state.pc++
        }
        0xA8 -> {
            state.a = state.a.xor(state.b)
            state.LogicFlagsA()
            state.pc++
        }
        0xA9 -> UnimplementedInstruction(state)
        0xAA -> UnimplementedInstruction(state)
        0xAB -> UnimplementedInstruction(state)
        0xAC -> UnimplementedInstruction(state)
        0xAD -> UnimplementedInstruction(state)
        0xAE -> UnimplementedInstruction(state)
        0xAF -> {                                    //XRA   A
            state.a = state.a.xor(state.a)
            state.LogicFlagsA()
            state.pc++
        }

        0xB0 -> {
            state.a = state.a.or(state.b)
            state.LogicFlagsA()
            state.pc++
        }
        0xB1 -> UnimplementedInstruction(state)
        0xB2 -> UnimplementedInstruction(state)
        0xB3 -> UnimplementedInstruction(state)
        0xB4 -> {                                   //ORA   H
            state.a = state.a.or(state.h)
            state.LogicFlagsA()
            state.pc++
        }
        0xB5 -> UnimplementedInstruction(state)
        0xB6 -> {
            state.a = state.a.or(state.readFromHL())
            state.pc++
        }
        0xB7 -> UnimplementedInstruction(state)
        0xB8 -> {                                   //CMP   B
            val res = (state.a - state.b).toUShort()
            state.ArithFlags(res)
            state.pc++
        }
        0xB9 -> UnimplementedInstruction(state)
        0xBA -> UnimplementedInstruction(state)
        0xBB -> UnimplementedInstruction(state)
        0xBC -> {                                   //CMP   H
            val res = (state.a - state.h).toUShort()
            state.ArithFlags(res)
            state.pc++
        }
        0xBD -> UnimplementedInstruction(state)
        0xBE -> {
            val res = state.a - state.readFromHL()
            state.ArithFlags(res.toUShort())
            state.pc++
        }
        0xBF -> UnimplementedInstruction(state)

        0xC0 -> {                                   //RNZ
            if (state.cc.z == 0u) {
                state.pc = (state.memory[state.sp.toInt()].toUInt()).or(state.memory[state.sp.toInt() + 1].toUInt().shl(8))
                state.sp = state.sp + 2u
            } else {
                state.pc++
            }
        }
        0xC1 -> {                                   //POP   B

            state.c = state.memory[state.sp.toInt()]
            state.b = state.memory[state.sp.toInt() + 1]
            state.sp = state.sp + 2u
            state.pc++
        }
        0xC2 -> {                                   //JNZ   address
            if (state.cc.z == 0u) {

                state.setPC()
            } else {

                state.pc = state.pc + 2u
                state.pc++
            }
        }
        0xC3 -> {
            state.setPC()
        }
        0xC4 -> {
            if(state.cc.z == 0u){
                val ret: UInt = state.pc + 3u
                state.WriteMem(state.sp.toInt() - 1, (ret.shr(8)).toUByte())
                state.WriteMem(state.sp.toInt() - 2, ret.and(0xffu).toUByte())
                state.sp = state.sp - 2u
                state.setPC()
            }else{
                state.pc+=3u
            }
        }
        0xC5 -> {                                   //PUSH  B
            state.Push(state.b, state.c)
            state.pc++
        }
        0xC6 -> {                                   //ADI   byte
            val res = (state.a.toUShort() + state.memory[state.pc.toInt() + 1].toUShort()).toUShort()
            state.flagsZSP((res.and(0xffu)).toUByte())
            if (res > 0xffu) {
                state.cc.cy = 1u
            } else {
                state.cc.cy = 0u
            }

            //TODO implement parity
            state.a = res.and(0xffu).toUByte()
            state.pc++
            state.pc++
        }
        0xC7 -> UnimplementedInstruction(state)
        0xC8 -> {                                   //RZ
            state.pc++
            if (state.cc.z == 1u) {
                state.pc = state.memory[state.sp.toInt()].toUInt().or(state.memory[state.sp.toInt() + 1].toUInt().shl(8))
                state.sp += 2u
            }


        }
        0xC9 -> {                                   //RET
           // val offset = (state.memory[state.sp.toInt()].toUInt()).or(state.memory[state.sp.toInt() + 1].toUInt().shl(8))

            val high = state.memory[state.sp.toInt()+1].toUInt().shl(8)
            val low = state.memory[state.sp.toInt()].toUInt()

            val offset = high.or(low)

            state.pc = offset
            state.sp = state.sp + 2u
        }
        0xCA -> {                                   //JZ
            if (state.cc.z==1u) {
                state.setPC()
            } else {
                state.pc += 3u
            }
        }
        0xCB -> UnimplementedInstruction(state)
        0xCC -> {                                   //CZ
            if(state.cc.z == 1u){
                val ret: UInt = state.pc + 3u
                state.WriteMem(state.sp.toInt() - 1, (ret.shr(8)).toUByte())
                state.WriteMem(state.sp.toInt() - 2, ret.and(0xffu).toUByte())
                state.sp = state.sp - 2u
                state.setPC()
            }else{
                state.pc+=3u
            }
        }
        0xCD -> {                                   //CALL address
            val ret: UInt = state.pc + 3u
            //println(String.format("%04x",ret.toInt()))
            val ret1 = ret.shr(8).toUByte()
            val ret2 = ret.and(0xffu).toUByte()
            state.WriteMem(state.sp.toInt() - 1, (ret.shr(8)).toUByte())
            state.WriteMem(state.sp.toInt() - 2, ret.and(0xffu).toUByte())
//            state.memory[state.sp.toInt()-1] = ret.shr(8).and(xFF.toUInt()).toUByte()
//            state.memory[state.sp.toInt()-2] = ret.and(xFF.toUInt()).toUByte()
            state.sp = state.sp - 2u
            state.setPC()
        }
        0xCE -> UnimplementedInstruction(state)
        0xCF -> UnimplementedInstruction(state)

        0xD0 -> {                                   //RNC
           state.pc++
            if (state.cc.cy == 0u) {
                val pointer = state.memory[state.sp.toInt()].toUInt().or(state.memory[state.sp.toInt() + 1].toUInt().shl(8))
                state.pc = pointer
                state.sp = (state.sp + 2u)
            }
        }
        0xD1 -> {                                   //POP   D
            state.e = state.memory[state.sp.toInt()]
            state.d = state.memory[state.sp.toInt() + 1]
            state.sp += 2u
            state.pc++
        }
        0xD2 -> {                                   //JNC
            if(state.cc.cy == 0u){
                state.setPC()
            }
            else{
                state.pc+=3u
            }
        }
        0xD3 -> {                                   //PUSH  D
            //not doing anything yet
            state.pc++
            state.pc++
        }
        0xD4 -> {                                   //CNC   adr
            if (state.cc.cy == 0u){
                val ret: UInt = state.pc + 3u
                state.WriteMem(state.sp.toInt() - 1, (ret.shr(8)).toUByte())
                state.WriteMem(state.sp.toInt() - 2, ret.and(0xffu).toUByte())
                state.sp = state.sp - 2u
                state.setPC()
            }else{
                state.pc+=3u
            }
        }
        0xD5 -> {                                   //PUSH  D
            state.Push(state.d, state.e)

            state.pc++
        }
        0xD6 -> {                                   //SUI
            val res = (state.a - state.memory[state.pc.toInt()+1].and(0xffu)).toUByte()
            state.flagsZSP(res)
            if(state.a < state.memory[state.pc.toInt()+1]){
                state.cc.cy = 1u
            }else{
                state.cc.cy = 0u
            }
            state.a = res
            state.pc+=2u
        }
        0xD7 -> UnimplementedInstruction(state)
        0xD8 -> {                                   //RC
            state.pc++
            if(state.cc.cy != 0u){
                state.pc = (state.memory[state.sp.toInt()].toUInt()).or(state.memory[state.sp.toInt()+1].toUInt().shl(8))
                state.sp +=2u
            }
        }
        0xD9 -> UnimplementedInstruction(state)
        0xDA -> {                                   //JC
            if (state.cc.cy != 0u) {
                state.setPC()
            } else {
                state.pc += 2u
                state.pc++
            }
        }
        0xDB -> UnimplementedInstruction(state)
        0xDC -> UnimplementedInstruction(state)
        0xDD -> UnimplementedInstruction(state)
        0xDE -> {                                   //SBI
            val res = (state.a -(state.memory[state.pc.toInt()+1] + state.cc.cy))
            state.flagsZSP((res).toUByte())
            if(res > 0xffu){
                state.cc.cy = 1u
            }else{
                state.cc.cy= 0u
            }
            state.a = res.toUByte()
            state.pc+=2u
        }
        0xDF -> UnimplementedInstruction(state)

        0xE0 -> UnimplementedInstruction(state)
        0xE1 -> {                                   //POP   H
            state.l = state.memory[state.sp.toInt()]
            state.h = state.memory[state.sp.toInt() + 1]
            state.sp = state.sp + 2u
            state.pc++
        }
        0xE2 -> UnimplementedInstruction(state)
        0xE3 -> {
            val h = state.h
            val l = state.l

            state.l = state.memory[state.sp.toInt()]
            state.h = state.memory[state.sp.toInt()+1]

            state.WriteMem(state.sp.toInt(), l)
            state.WriteMem(state.sp.toInt()+1, h)

            state.pc++
        }
        0xE4 -> UnimplementedInstruction(state)
        0xE5 -> {                                   //PUSH  H
            state.Push(state.h, state.l)
            state.pc++
        }
        0xE6 -> {                                   //ANI   byte
            state.a = (state.a.and(state.memory[state.pc.toInt() + 1]))
            state.LogicFlagsA()
            state.pc++
            state.pc++
        }
        0xE7 -> UnimplementedInstruction(state)
        0xE8 -> UnimplementedInstruction(state)
        0xE9 -> {                                   //PCHL
            state.pc = (state.h.toUInt().shl(8)).or(state.l.toUInt())
        }
        0xEA -> UnimplementedInstruction(state)
        0xEB -> {                                   //XCHG
            val save1 = state.d
            val save2 = state.e
            state.d = state.h
            state.e = state.l
            state.h = save1
            state.l = save2
            state.pc++
        }
        0xEC -> UnimplementedInstruction(state)
        0xED -> UnimplementedInstruction(state)
        0xEE -> UnimplementedInstruction(state)
        0xEF -> UnimplementedInstruction(state)

        0xF0 -> UnimplementedInstruction(state)
        0xF1 -> {                                   //POP psw

            state.a = state.memory[state.sp.toInt() + 1]
            val psw = state.memory[state.sp.toInt()]

            if (psw.and(0x01u) == 0x01u.toUByte()) {
                state.cc.z = 1u
            } else {
                state.cc.z = 0u
            }

            if (psw.and(0x02u) == 0x02u.toUByte()) {
                state.cc.s = 1u
            } else {
                state.cc.s = 0u
            }

            if (psw.and(0x04u) == 0x04.toUByte()) {
                state.cc.p = 1u
            } else {
                state.cc.p = 0u
            }

            if (psw.and(0x08u) == 0x05u.toUByte()) {
                state.cc.cy = 1u
            } else {
                state.cc.cy = 0u
            }

            if (psw.and(0x10u) == 0x10u.toUByte()) {
                state.cc.ac = 1u
            } else {
                state.cc.ac = 0u
            }
            state.sp = state.sp + 2u
            state.pc++
        }
        0xF2 -> UnimplementedInstruction(state)
        0xF3 -> UnimplementedInstruction(state)
        0xF4 -> UnimplementedInstruction(state)
        0xF5 -> {                                   //PUSH  PSW
            //println(state.sp.toInt()-1)

            state.memory[state.sp.toInt() - 1] = state.a
            val psw: UByte = (state.cc.z
                    .or(state.cc.s).shl(1)
                    .or(state.cc.p).shl(2)
                    .or(state.cc.cy).shl(3)
                    .or(state.cc.ac).shl(4))
                    .toUByte()
            state.memory[state.sp.toInt() - 2] = psw
            state.sp = state.sp - 2u
            state.pc++
        }
        0xF6 -> {                                   //ORI
            val res = state.a.or(state.memory[state.pc.toInt()+1])
            state.flagsZSP(res)
            state.cc.cy = 0u
            state.a = res
            state.pc+=2u
        }
        0xF7 -> UnimplementedInstruction(state)
        0xF8 -> UnimplementedInstruction(state)
        0xF9 -> UnimplementedInstruction(state)
        0xFA -> {                                   //JM
            if(state.cc.s != 0u){
                state.setPC()
            }else{
                state.pc+=3u
            }
        }
        0xFB -> {                                   //EI
            state.int_enable = 1
            state.pc++
        }
        0xFC -> UnimplementedInstruction(state)
        0xFD -> UnimplementedInstruction(state)
        0xFE -> {


            val value = (state.a - state.memory[state.pc.toInt() + 1]).toUByte()
            state.flagsZSP(value)

            if (state.a < state.memory[state.pc.toInt() + 1]) {
                state.cc.cy = 1u
            } else {
                state.cc.cy = 0u
            }
            state.pc++
            state.pc++

        }
        0xFF -> UnimplementedInstruction(state)
    }


    return cycles8080[opcode]

}

