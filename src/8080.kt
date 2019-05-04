import java.awt.*
import java.awt.image.BufferedImage
import java.awt.image.BufferedImage.TYPE_BYTE_BINARY
import java.io.File
import java.util.*
import javax.swing.*
import kotlin.system.exitProcess


@kotlin.ExperimentalUnsignedTypes
const val x01:UByte = 1u
const val x02:UByte = 2u
const val x04:UByte = 4u
const val x05:UByte = 5u
const val x08:UByte = 8u
const val x10:UByte = 16u
const val xFF:UShort = 255u
const val x80:UShort = 128u
const val xFFb:UByte = 254u
const val x80b:UByte = 128u
const val xFF00:UInt = 65280u
const val xFF00s:UShort = 65280u
const val x00FF:UInt = 255u
const val xFFFF:UInt = 65535u
const val xFFFF0000:UInt = 4294901760u
var opcount = 0

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
        if(value.and(xFF).compareTo(0u) == 0){
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
        if(value.and(x80).compareTo(0u) == 0){
            cc.s = 0u
        }else{
            cc.s = 1u
        }
    }
    fun setSign(value:UByte){
        if(value.and(x80b).compareTo(0u) == 0){
            cc.s = 0u
        }else{
            cc.s = 1u
        }
    }

    fun setCarry(value:UShort){
        if(value > xFF){
            cc.cy = 1u
        }else{
            cc.cy = 0u
        }
    }
    fun setCarry(value:UByte){
        if(value > xFFb){
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
        setCarry(a)
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

        if(0x80u.compareTo(res.and(0x80u)) == 0){
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

        memory[address] = value


    }
    fun GenerateInterupt(interupt_num:Int){
        val high = (pc.and(0xFF00u).shr(8)).toUByte()
        val low = (pc .and(0x00FFu)).toUByte()
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
    val img = BufferedImage(256,224,TYPE_BYTE_BINARY)
    var i = 0
    var j = 0
    val offset = 0x2400
    for(x in 0 until img.getWidth()){
        for(y in 0 until img.getHeight()){
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

//    if(byte.shr(position).and(1) != 0){
//        println("yes")
//    }
    return(byte.shr(position).and(1))
}
@kotlin.ExperimentalUnsignedTypes
fun main() {
    val screen = JFrame("Space Invaders")
    var display = JLabel(ImageIcon())
    screen.getContentPane().add(display,BorderLayout.CENTER)
    screen.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)
    screen.setSize(Dimension(256,224))
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
        var opcode = String.format("%02x",emulate.memory[emulate.pc.toInt()].toByte()).toUpperCase()
        if(opcode == "DB"){
//            println(opcode + "  "+ String.format("%04x", emulate.pc.toInt()) )

            var port = emulate.memory[emulate.pc.toInt()+1]
            emulate.SpaceInvadersIn(port)
            emulate.pc++
            emulate.pc++
        }else if(opcode == "D3"){
//            println(opcode + "  "+ String.format("%04x", emulate.pc.toInt()) )

            var port = emulate.memory[emulate.pc.toInt()+1]
            emulate.SpaceInvadersOut(port)
            emulate.pc++
            emulate.pc++
        }else{

            Emulate8080Op(emulate)


        }
        opcount++
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

@kotlin.ExperimentalUnsignedTypes
fun Emulate8080Op(state:Registers) {
    var opcode = String.format("%02x", state.memory[state.pc.toInt()].toByte()).toUpperCase()
    //println(opcode + "  " + String.format("%04x", state.pc.toInt()))
    when (opcode) {
        "00" -> {
            state.pc++
        } // NOP
        "01" -> {                                   //LXI   B,byte
            state.c = state.memory[state.pc.toInt() + 1]
            state.b = state.memory[state.pc.toInt() + 2]
            state.pc = state.pc + 2u
            state.pc++
        }
        "02" -> {                                   //STAX  B
            state.pc++
            val offset = (state.b.toInt().shl(8)).or(state.c.toInt())
            //println(String.format("%04x", state.b.toInt()))
            state.WriteMem(offset, state.a)

        }
        "03" -> {                                   //INX   B
            state.c++
            if (state.c.toInt() == 0) {
                state.b++
            }
            state.pc++
        }
        "04" -> {                                   //INR   B
            state.b++
            state.flagsZSP(state.b)
            state.pc++
        }
        "05" -> {                                   //DCR   B
            state.b--
            state.flagsZSP(state.b)
            state.pc++
        }
        "06" -> {                                   //MVI   B,byte
            state.b = state.memory[state.pc.toInt() + 1]
            state.pc++
            state.pc++
        }
        "07" -> {
            val res = state.a.toUInt()
            state.a =  ((res.and(0x80u).shr(7)).or(res.shl(1))).toUByte()
            if (1u == (res.and(1u))) {
                state.cc.cy = 1u
            } else {
                state.cc.cy = 0u
            }
            state.pc++
        }
        "08" -> UnimplementedInstruction(state)
        "09" -> {                                   //DAD   B
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
        "0A" -> {                                   //LDAX B
            val offset = (state.b.toUInt().shl(8).or(state.c.toUInt()))
            state.a = state.memory[offset.toInt()]
            state.pc++
        }
        "0B" -> UnimplementedInstruction(state)
        "0C" -> UnimplementedInstruction(state)
        "0D" -> {                                   //DCR   C

            state.c--
            state.flagsZSP(state.c)
            state.pc++
        }
        "0E" -> {                                   //MVI   C,byte

            state.c = state.memory[state.pc.toInt() + 1]
            state.pc++
            state.pc++
        }
        "0F" -> {                                   //RRC

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

        "10" -> UnimplementedInstruction(state)
        "11" -> {
            state.e = state.memory[state.pc.toInt() + 1]
            state.d = state.memory[state.pc.toInt() + 2]
            state.pc = state.pc + 2u
            state.pc++
        }
        "12" -> UnimplementedInstruction(state)
        "13" -> {                                       //INX   D
            state.e++
            if (state.e.toInt() == 0) {
                state.d++
            }
            state.pc++
        }
        "14" -> {                                       //INR   D
            state.d++
            state.flagsZSP(state.d)
            state.pc++
        }
        "15" -> {                                       //DCR   D
            state.d--
            state.flagsZSP(state.d)
            state.pc++
        }
        "16" -> {
            state.d = state.memory[state.pc.toInt()+1]
            state.pc+=2u
        }
        "17" -> {
            val x = state.a
            state.a = (state.cc.cy.or(x.toUInt().shl(1))).toUByte()
            if (0x80 == (x.and(0x80u)).toInt()) {
                state.cc.cy = 1u
            } else {
                state.cc.cy = 0u
            }
            state.pc++
        }
        "18" -> UnimplementedInstruction(state)
        "19" -> {                                   //DAD   D
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
        "1A" -> {                               //LDAX	D
            val offset = (state.d.toInt().shl(8)).or(state.e.toInt())
            state.a = state.memory[offset]
            state.pc++
        }
        "1B" -> UnimplementedInstruction(state)
        "1C" -> UnimplementedInstruction(state)
        "1D" -> UnimplementedInstruction(state)
        "1E" -> UnimplementedInstruction(state)
        "1F" -> {                               //RAR
            val res = state.a
            state.a = ((state.cc.cy).shl(7).or(res.toUInt().shr(1))).toUByte()
            if(1u.toUByte() == res.and(1u)){
                state.cc.cy = 1u
            }else{
                state.cc.cy = 0u
            }
            state.pc++
        }

        "20" -> UnimplementedInstruction(state)
        "21" -> {
            state.l = state.memory[state.pc.toInt() + 1]
            state.h = state.memory[state.pc.toInt() + 2]
            state.pc = state.pc + 2u
            state.pc++
        }
        "22" -> {
            val offset = (state.memory[state.pc.toInt()+1].toUInt()).or(state.memory[state.pc.toInt()+2].toUInt().shl(8))
            state.WriteMem(offset.toInt(), state.l)
            state.WriteMem(offset.toInt()+1,state.h)
            state.pc+=3u
        }
        "23" -> {                           //INX   H
            state.l++
            if (state.l.toInt() == 0) {
                state.h++
            }
            state.pc++
        }
        "24" -> UnimplementedInstruction(state)
        "25" -> UnimplementedInstruction(state)
        "26" -> {                                   //MVI   H,byte
            state.h = state.memory[state.pc.toInt() + 1]
            state.pc++
            state.pc++
        }
        "27" -> UnimplementedInstruction(state)
        "28" -> UnimplementedInstruction(state)
        "29" -> {                                   //DAD   HL
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
        "2A" -> {
            val offset = (state.memory[state.pc.toInt()+1].toUInt()).or(state.memory[state.pc.toInt()+2].toUInt().shl(8))
            state.l = state.memory[offset.toInt()]
            state.h = state.memory[offset.toInt()+1]
            state.pc+=3u
        }
        "2B" -> {
            state.l--
            if(state.l == 0xffu.toUByte()){
                state.h--
            }
            state.pc++
        }
        "2C" -> UnimplementedInstruction(state)
        "2D" -> UnimplementedInstruction(state)
        "2E" -> {
            state.l = state.memory[state.pc.toInt()+1]
            state.pc+=2u
        }
        "2F" -> {                                   //CMA
            state.a = state.a.inv()
            state.pc++
        }

        "30" -> UnimplementedInstruction(state)
        "31" -> {                                   //LXI   SP
            state.sp = (state.memory[state.pc.toInt() + 2].toUInt().shl(8)).or(state.memory[state.pc.toInt() + 1].toUInt())

            state.pc++
            state.pc++
            state.pc++
        }
        "32" -> {                                   //STA   byte
            val offset = (state.memory[state.pc.toInt() + 2].toInt()).shl(8)
                    .or(state.memory[state.pc.toInt() + 1].toInt())
            val temp1 = state.memory[state.pc.toInt() + 2]
            val temp2 = state.memory[state.pc.toInt() + 1]
            state.WriteMem(offset, state.a)
            state.pc = state.pc + 2u
            state.pc++
        }
        "33" -> UnimplementedInstruction(state)
        "34" -> {                                   //INR   (HL)
            val res = (state.readFromHL() +1u).toUByte()
            state.flagsZSP(res)
            state.writeToHL(res)
            state.pc++
        }
        "35" -> {
            val res = (state.readFromHL() - 1u).toUByte()
            state.setZero(res)
            state.setSign(res)
            //TODO implemet parity
            state.writeToHL(res)
            state.pc++
        }
        "36" -> {                                   //MVI   M,byte
            val offset = state.h.toUInt().shl(8).or(state.l.toUInt())
            state.memory[offset.toInt()] = state.memory[state.pc.toInt() + 1]
            state.pc++
            state.pc++
        }
        "37" -> {
            state.cc.cy = 1u
            state.pc++
        }
        "38" -> UnimplementedInstruction(state)
        "39" -> {                                   //DAD   SP
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
        "3A" -> {                                   //LDA   byte
            val offset = (state.memory[state.pc.toInt() + 2].toUInt()).shl(8)
                    .or(state.memory[state.pc.toInt() + 1].toUInt())
            //println(String.format("%04x", offset))
            //println("aaa" + state.memory[offset.toInt()])
            state.a = state.memory[offset.toInt()]
            state.pc = state.pc + 2u
            state.pc++
        }
        "3B" -> UnimplementedInstruction(state)
        "3C" -> {                                   //INR   A
            state.a++
            state.flagsZSP(state.a)
            state.pc++
        }
        "3D" -> {                                   //DCR   A
            state.a--
            state.flagsZSP(state.a)
            state.pc++
        }
        "3E" -> {                                   //MVI   A,byte
            state.a = state.memory[state.pc.toInt() + 1]
            state.pc++
            state.pc++

        }
        "3F" -> UnimplementedInstruction(state)

        "40" -> UnimplementedInstruction(state)
        "41" -> UnimplementedInstruction(state)
        "42" -> UnimplementedInstruction(state)
        "43" -> UnimplementedInstruction(state)
        "44" -> UnimplementedInstruction(state)
        "45" -> UnimplementedInstruction(state)
        "46" -> {
            state.b = state.readFromHL()
            state.pc++
        }
        "47" -> {
            state.b = state.a
            state.pc++
        }
        "48" -> UnimplementedInstruction(state)
        "49" -> UnimplementedInstruction(state)
        "4A" -> UnimplementedInstruction(state)
        "4B" -> UnimplementedInstruction(state)
        "4C" -> UnimplementedInstruction(state)
        "4D" -> UnimplementedInstruction(state)
        "4E" -> {
            state.c = state.readFromHL()
            state.pc++
        }
        "4F" -> {
            state.c = state.a
            state.pc++
        }

        "50" -> UnimplementedInstruction(state)
        "51" -> UnimplementedInstruction(state)
        "52" -> UnimplementedInstruction(state)
        "53" -> UnimplementedInstruction(state)
        "54" -> UnimplementedInstruction(state)
        "55" -> UnimplementedInstruction(state)
        "56" -> {                                   //MOV   D,M
            state.d = state.memory[state.offsetHL().toInt()]
            state.pc++
        }
        "57" -> {
            state.d = state.a
            state.pc++
        }
        "58" -> UnimplementedInstruction(state)
        "59" -> UnimplementedInstruction(state)
        "5A" -> UnimplementedInstruction(state)
        "5B" -> UnimplementedInstruction(state)
        "5C" -> UnimplementedInstruction(state)
        "5D" -> UnimplementedInstruction(state)
        "5E" -> {                                   //MOV   E,M
            state.e = state.memory[state.offsetHL().toInt()]
            state.pc++
        }
        "5F" -> {
            state.e = state.a
            state.pc++
        }

        "60" -> UnimplementedInstruction(state)
        "61" -> {                                  //MOV    H,C
            state.h = state.c
            state.pc++
        }
        "62" -> UnimplementedInstruction(state)
        "63" -> UnimplementedInstruction(state)
        "64" -> UnimplementedInstruction(state)
        "65" -> {
            state.h = state.l
            state.pc++
        }
        "66" -> {                                   //MOV   H,M
            state.h = state.memory[state.offsetHL().toInt()]
            state.pc++
        }
        "67" -> {
            state.h = state.a
            state.pc++
        }
        "68" -> {                               //MOV   L,B
            state.l = state.b
            state.pc++
        }
        "69" -> {                                   //
            state.l = state.c
            state.pc++
        }
        "6A" -> UnimplementedInstruction(state)
        "6B" -> UnimplementedInstruction(state)
        "6C" -> UnimplementedInstruction(state)
        "6D" -> UnimplementedInstruction(state)
        "6E" -> UnimplementedInstruction(state)
        "6F" -> {                                   //MOV   L,A
            state.l = state.a
            state.pc++
        }

        "70" -> {                                   //MOV (HL), B
            state.writeToHL(state.b)
            state.pc++
        }
        "71" -> UnimplementedInstruction(state)
        "72" -> UnimplementedInstruction(state)
        "73" -> UnimplementedInstruction(state)
        "74" -> UnimplementedInstruction(state)
        "75" -> UnimplementedInstruction(state)
        "76" -> UnimplementedInstruction(state)
        "77" -> {                                   //MOV   M,A
            state.writeToHL(state.a)
            state.pc++

        }
        "78" -> {
            state.a = state.b
            state.pc++
        }
        "79" -> {
            state.a = state.c
            state.pc++
        }
        "7A" -> {                                   //MOV   D,A
            state.a = state.d
            state.pc++
        }
        "7B" -> {                                   //MOV   E,A
            state.a = state.e
            state.pc++
        }
        "7C" -> {                                    //MOV   A,H
            state.a = state.h
            state.pc++
        }
        "7D" -> {                                   //
            state.a = state.l
            state.pc++
        }
        "7E" -> {                                   //MOV   A,M
            state.a = state.memory[state.offsetHL().toInt()]
            state.pc++
        }
        "7F" -> UnimplementedInstruction(state)

        "80" -> {
            val res: UShort = (state.a.toUShort() + state.b.toUShort()).toUShort()
            state.ArithFlags(res)
            state.a = res.and(0xffu).toUByte()
            state.pc++
        }
        "81" -> {
            val res: UShort = (state.a.toUShort() + state.c.toUShort()).toUShort()
            state.ArithFlags(res)
            state.a = res.and(0xffu).toUByte()
            state.pc++
        }
        "82" -> {
            val res: UShort = (state.a.toUShort() + state.d.toUShort()).toUShort()
            state.ArithFlags(res)
            state.a = res.and(0xffu).toUByte()
            state.pc++
        }
        "83" -> {
            val res: UShort = (state.a.toUShort() + state.e.toUShort()).toUShort()
            state.ArithFlags(res)
            state.a = res.and(0xffu).toUByte()
            state.pc++
        }
        "84" -> {
            val res: UShort = (state.a.toUShort() + state.h.toUShort()).toUShort()
            state.ArithFlags(res)
            state.a = res.and(0xffu).toUByte()
            state.pc++
        }
        "85" -> {
            val res: UShort = (state.a.toUShort() + state.l.toUShort()).toUShort()
            state.ArithFlags(res)
            state.a = res.and(0xffu).toUByte()
            state.pc++
        }
        "86" -> {                                   //ADD   (HL)
            val res = (state.a + state.readFromHL()).toUShort()
            state.ArithFlags(res)
            state.a = res.toUByte()
            state.pc++
        }
        "87" -> UnimplementedInstruction(state)
        "88" -> UnimplementedInstruction(state)
        "89" -> UnimplementedInstruction(state)
        "8A" -> UnimplementedInstruction(state)
        "8B" -> UnimplementedInstruction(state)
        "8C" -> UnimplementedInstruction(state)
        "8D" -> UnimplementedInstruction(state)
        "8E" -> UnimplementedInstruction(state)
        "8F" -> UnimplementedInstruction(state)

        "90" -> UnimplementedInstruction(state)
        "91" -> UnimplementedInstruction(state)
        "92" -> UnimplementedInstruction(state)
        "93" -> UnimplementedInstruction(state)
        "94" -> UnimplementedInstruction(state)
        "95" -> UnimplementedInstruction(state)
        "96" -> UnimplementedInstruction(state)
        "97" -> UnimplementedInstruction(state)
        "98" -> UnimplementedInstruction(state)
        "99" -> UnimplementedInstruction(state)
        "9A" -> UnimplementedInstruction(state)
        "9B" -> UnimplementedInstruction(state)
        "9C" -> UnimplementedInstruction(state)
        "9D" -> UnimplementedInstruction(state)
        "9E" -> UnimplementedInstruction(state)
        "9F" -> UnimplementedInstruction(state)

        "A0" ->{
            state.a = state.a.and(state.b)
            state.LogicFlagsA()
            state.pc++
        }
        "A1" -> UnimplementedInstruction(state)
        "A2" -> UnimplementedInstruction(state)
        "A3" -> UnimplementedInstruction(state)
        "A4" -> UnimplementedInstruction(state)
        "A5" -> UnimplementedInstruction(state)
        "A6" -> {                                   //ANA   (HL)
            state.a = state.a.and(state.readFromHL())
            state.LogicFlagsA()
            state.pc++
        }
        "A7" -> {                                   //ANA   A
            state.a = state.a.and(state.a)
            state.LogicFlagsA()
            state.pc++
        }
        "A8" -> {
            state.a = state.a.xor(state.b)
            state.LogicFlagsA()
            state.pc++
        }
        "A9" -> UnimplementedInstruction(state)
        "AA" -> UnimplementedInstruction(state)
        "AB" -> UnimplementedInstruction(state)
        "AC" -> UnimplementedInstruction(state)
        "AD" -> UnimplementedInstruction(state)
        "AE" -> UnimplementedInstruction(state)
        "AF" -> {                                    //XRA   A
            state.a = state.a.xor(state.a)
            state.LogicFlagsA()
            state.pc++
        }

        "B0" -> {
            state.a = state.a.or(state.b)
            state.LogicFlagsA()
            state.pc++
        }
        "B1" -> UnimplementedInstruction(state)
        "B2" -> UnimplementedInstruction(state)
        "B3" -> UnimplementedInstruction(state)
        "B4" -> {                                   //ORA   H
            state.a = state.a.or(state.h)
            state.LogicFlagsA()
            state.pc++
        }
        "B5" -> UnimplementedInstruction(state)
        "B6" -> {
            state.a = state.a.or(state.readFromHL())
            state.pc++
        }
        "B7" -> UnimplementedInstruction(state)
        "B8" -> {                                   //CMP   B
            val res = (state.a - state.b).toUShort()
            state.ArithFlags(res)
            state.pc++
        }
        "B9" -> UnimplementedInstruction(state)
        "BA" -> UnimplementedInstruction(state)
        "BB" -> UnimplementedInstruction(state)
        "BC" -> UnimplementedInstruction(state)
        "BD" -> UnimplementedInstruction(state)
        "BE" -> {
            val res = state.a - state.readFromHL()
            state.ArithFlags(res.toUShort())
            state.pc++
        }
        "BF" -> UnimplementedInstruction(state)

        "C0" -> {                                   //RNZ
            if (state.cc.z == 0u) {
                state.pc = (state.memory[state.sp.toInt()].toUInt()).or(state.memory[state.sp.toInt() + 1].toUInt().shl(8))
                state.sp = state.sp + 2u
            } else {
                state.pc++
            }
        }
        "C1" -> {                                   //POP   B

            state.c = state.memory[state.sp.toInt()]
            state.b = state.memory[state.sp.toInt() + 1]
            state.sp = state.sp + 2u
            state.pc++
        }
        "C2" -> {                                   //JNZ   address
            if (state.cc.z == 0u) {

                state.setPC()
            } else {

                state.pc = state.pc + 2u
                state.pc++
            }
        }
        "C3" -> {
            state.setPC()
        }
        "C4" -> {
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
        "C5" -> {                                   //PUSH  B
            state.Push(state.b, state.c)
            state.pc++
        }
        "C6" -> {                                   //ADI   byte
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
        "C7" -> UnimplementedInstruction(state)
        "C8" -> {                                   //RZ
            state.pc++
            if (state.cc.z == 1u) {
                state.pc = state.memory[state.sp.toInt()].toUInt().or(state.memory[state.sp.toInt() + 1].toUInt().shl(8))
                state.sp += 2u
            }


        }
        "C9" -> {                                   //RET
           // val offset = (state.memory[state.sp.toInt()].toUInt()).or(state.memory[state.sp.toInt() + 1].toUInt().shl(8))

            val high = state.memory[state.sp.toInt()+1].toUInt().shl(8)
            val low = state.memory[state.sp.toInt()].toUInt()

            val offset = high.or(low)

            state.pc = offset
            state.sp = state.sp + 2u
        }
        "CA" -> {                                   //JZ
            if (state.cc.z==1u) {
                state.setPC()
            } else {
                state.pc += 3u
            }
        }
        "CB" -> UnimplementedInstruction(state)
        "CC" -> {                                   //CZ
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
        "CD" -> {                                   //CALL address
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
        "CE" -> UnimplementedInstruction(state)
        "CF" -> UnimplementedInstruction(state)

        "D0" -> {                                   //RNC
           state.pc++
            if (state.cc.cy == 0u) {
                val pointer = state.memory[state.sp.toInt()].toUInt().or(state.memory[state.sp.toInt() + 1].toUInt().shl(8))
                state.pc = pointer
                state.sp = (state.sp + 2u)
            }
        }
        "D1" -> {                                   //POP   D
            state.e = state.memory[state.sp.toInt()]
            state.d = state.memory[state.sp.toInt() + 1]
            state.sp += 2u
            state.pc++
        }
        "D2" -> {                                   //JNC
            if(state.cc.cy == 0u){
                state.setPC()
            }
            else{
                state.pc+=3u
            }
        }
        "D3" -> {                                   //PUSH  D
            //not doing anything yet
            state.pc++
            state.pc++
        }
        "D4" -> UnimplementedInstruction(state)
        "D5" -> {                                   //PUSH  D
            state.Push(state.d, state.e)

            state.pc++
        }
        "D6" -> {                                   //SUI
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
        "D7" -> UnimplementedInstruction(state)
        "D8" -> {                                   //RC
            state.pc++
            if(state.cc.cy != 0u){
                state.pc = (state.memory[state.sp.toInt()].toUInt()).or(state.memory[state.sp.toInt()+1].toUInt().shl(8))
                state.sp +=2u
            }
        }
        "D9" -> UnimplementedInstruction(state)
        "DA" -> {                                   //JC
            if (state.cc.cy != 0u) {
                state.setPC()
            } else {
                state.pc += 2u
                state.pc++
            }
        }
        "DB" -> UnimplementedInstruction(state)
        "DC" -> UnimplementedInstruction(state)
        "DD" -> UnimplementedInstruction(state)
        "DE" -> {                                   //SBI
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
        "DF" -> UnimplementedInstruction(state)

        "E0" -> UnimplementedInstruction(state)
        "E1" -> {                                   //POP   H
            state.l = state.memory[state.sp.toInt()]
            state.h = state.memory[state.sp.toInt() + 1]
            state.sp = state.sp + 2u
            state.pc++
        }
        "E2" -> UnimplementedInstruction(state)
        "E3" -> {
            val h = state.h
            val l = state.l

            state.l = state.memory[state.sp.toInt()]
            state.h = state.memory[state.sp.toInt()+1]

            state.WriteMem(state.sp.toInt(), l)
            state.WriteMem(state.sp.toInt()+1, h)

            state.pc++
        }
        "E4" -> UnimplementedInstruction(state)
        "E5" -> {                                   //PUSH  H
            state.Push(state.h, state.l)
            state.pc++
        }
        "E6" -> {                                   //ANI   byte
            state.a = (state.a.and(state.memory[state.pc.toInt() + 1]))
            state.LogicFlagsA()
            state.pc++
            state.pc++
        }
        "E7" -> UnimplementedInstruction(state)
        "E8" -> UnimplementedInstruction(state)
        "E9" -> {                                   //PCHL
            state.pc = (state.h.toUInt().shl(8)).or(state.l.toUInt())
        }
        "EA" -> UnimplementedInstruction(state)
        "EB" -> {                                   //XCHG
            val save1 = state.d
            val save2 = state.e
            state.d = state.h
            state.e = state.l
            state.h = save1
            state.l = save2
            state.pc++
        }
        "EC" -> UnimplementedInstruction(state)
        "ED" -> UnimplementedInstruction(state)
        "EE" -> UnimplementedInstruction(state)
        "EF" -> UnimplementedInstruction(state)

        "F0" -> UnimplementedInstruction(state)
        "F1" -> {                                   //POP psw

            state.a = state.memory[state.sp.toInt() + 1]
            val psw = state.memory[state.sp.toInt()]

            if (psw.and(x01) == x01) {
                state.cc.z = 1u
            } else {
                state.cc.z = 0u
            }

            if (psw.and(x02) == x02) {
                state.cc.s = 1u
            } else {
                state.cc.s = 0u
            }

            if (psw.and(x04) == x04) {
                state.cc.p = 1u
            } else {
                state.cc.p = 0u
            }

            if (psw.and(x08) == x05) {
                state.cc.cy = 1u
            } else {
                state.cc.cy = 0u
            }

            if (psw.and(x10) == x10) {
                state.cc.ac = 1u
            } else {
                state.cc.ac = 0u
            }
            state.sp = state.sp + 2u
            state.pc++
        }
        "F2" -> UnimplementedInstruction(state)
        "F3" -> UnimplementedInstruction(state)
        "F4" -> UnimplementedInstruction(state)
        "F5" -> {                                   //PUSH  PSW
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
        "F6" -> {                                   //ORI
            val res = state.a.or(state.memory[state.pc.toInt()+1])
            state.flagsZSP(res)
            state.cc.cy = 0u
            state.a = res
            state.pc+=2u
        }
        "F7" -> UnimplementedInstruction(state)
        "F8" -> UnimplementedInstruction(state)
        "F9" -> UnimplementedInstruction(state)
        "FA" -> {                                   //JM
            if(state.cc.s != 0u){
                state.setPC()
            }else{
                state.pc+=3u
            }
        }
        "FB" -> {                                   //EI
            state.int_enable = 1
            state.pc++
        }
        "FC" -> UnimplementedInstruction(state)
        "FD" -> UnimplementedInstruction(state)
        "FE" -> {
            val val2 = state.memory[state.pc.toInt() + 1]
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
        "FF" -> UnimplementedInstruction(state)
    }




}

