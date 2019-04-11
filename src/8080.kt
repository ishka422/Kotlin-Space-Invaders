import java.io.File
import java.util.*
import kotlin.system.exitProcess

const val x01:UByte = 1u
const val x02:UByte = 2u
const val x04:UByte = 4u
const val x05:UByte = 5u
const val x08:UByte = 8u
const val x10:UByte = 16u
const val xFF:UShort = 255u
const val x80:UShort = 128u
const val xFFb:UByte = 255u
const val x80b:UByte = 128u
const val xFF00:UInt = 65280u
const val xFFFF:UInt = 65535u


class ConditionCodes(){
    var z:UInt   = 0u
    var s:UInt   = 0u
    var p:UInt   = 0u
    var cy:UInt  = 0u
    var ac:UInt  = 0u
    var pad:UInt = 0u

}

class Register(var value:UByte)

class Registers(){

    var a:UByte = 0u
    var b:UByte = 0u
    var c:UByte = 0u
    var d:UByte = 0u
    var e:UByte = 0u
    var h:UByte = 0u
    var l:UByte = 0u
    var sp:UShort = 0u
    var pc:UShort = 0u
    var memory:UByteArray = UByteArray(65535)       //memory for machine, needs to be an array, size should be 0x2000
    var cc = ConditionCodes()
    var int_enable:UByte = 0u

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
        if(value.and(xFFb).compareTo(0u) == 0){
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
        var upperByte = (memory[pc.toInt()+2]).toUInt()
        val lowerByte = memory[pc.toInt()+1].toUInt()
        upperByte = upperByte.shl(8)
        val combined = upperByte.or(lowerByte)-1u

        pc = combined.toUShort()
    }
    fun setSP(){
        var upperByte = (memory[pc.toInt()+2]).toUInt()
        val lowerByte = memory[pc.toInt()+1].toUInt()
        upperByte = upperByte.shl(8)
        val combined = upperByte.or(lowerByte)

        sp = combined.toUShort()
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
}


fun main() {


    val bytes:UByteArray = File("invaders").readBytes().toUByteArray()
    disassemble(bytes)
    var emulate = Registers()
    println(emulate.memory.size)
    for(i in 0 until bytes.size){
        emulate.memory[i] = bytes[i]
    }


    //for(i in bytes){
     //   println(String.format("%02x", i.toByte()))
   // }
    while(emulate.pc.toInt()<bytes.size){
        Emulate8080Op(emulate)
    }

}
fun UnimplementedInstruction(state:Registers){
    println(String.format("Error Unimplemented instruction:\t%02x\n",state.memory[state.pc.toInt()].toByte()))
    exitProcess(2)
}

fun Emulate8080Op(state:Registers){
    var opcode = String.format("%02x",state.memory[state.pc.toInt()].toByte()).toUpperCase()
    println(opcode + "  "+ String.format("%04x", state.pc.toInt()) )
    when(opcode){
        "00" ->  {
            state.a = state.a
            } // NOP
        "01" -> {                                   //LXI   B,byte
            state.c = state.memory[state.pc.toInt()+1]
            state.b = state.memory[state.pc.toInt()+2]
            state.pc = (state.pc + 2u).toUShort()
        }
        "02" -> UnimplementedInstruction(state)
        "03" -> UnimplementedInstruction(state)
        "04" -> UnimplementedInstruction(state)
        "05" -> {                                   //DCR   B
            val res:UByte = (state.b-1u).toUByte()
            state.setZero(res)
            state.setSign(res)
            //TODO implement parity
            state.b = res
        }
        "06" -> {                                   //MVI   B,byte
            state.b = state.memory[state.pc.toInt() + 1]
            state.pc++
        }
        "07" -> UnimplementedInstruction(state)
        "08" -> UnimplementedInstruction(state)
        "09" -> {                                   //DAD   B
            val hl:UInt = (state.h.toUInt().shl(8).or(state.l.toUInt()))
            val bc:UInt = (state.b.toUInt().shl(8).or(state.c.toUInt()))
            val res = hl + bc
            state.h = (res.and(xFF00).shr(8)).toUByte()
            state.l = res.and(xFF.toUInt()).toUByte()
            if(res>xFFFF){
                state.cc.cy = 1u
            }else{
                state.cc.cy = 0u
            }
        }
        "0A" -> UnimplementedInstruction(state)
        "0B" -> UnimplementedInstruction(state)
        "0C" -> UnimplementedInstruction(state)
        "0D" -> {                                   //DCR   C
            val res = (state.c -1u).toUByte()
            state.setZero(res)
            state.setSign(res)
            //TODO implement parity
            state.c = res
        }
        "0E" -> {                                   //MVI   C,byte
            state.c = state.memory[state.pc.toInt()+1]
            state.pc++
        }
        "0F" -> {                                   //RRC
            val x = state.a.and(1u)
            val temp = state.a.toUInt()
            state.a = (temp.shr(1).and(x.toUInt().shr(7))).toUByte()
            if(x.toInt() == 0){
                state.cc.cy = 0u
            }else{
                state.cc.cy = 1u
            }
        }

        "10" -> UnimplementedInstruction(state)
        "11" -> {
            state.e = state.memory[state.pc.toInt()+1]
            state.d = state.memory[state.pc.toInt()+2]
            state.pc= (state.pc+2u).toUShort()
        }
        "12" -> UnimplementedInstruction(state)
        "13" -> {                                       //INX   D
            state.e++
            if(state.e.toInt() == 0){
                state.d++
            }
        }
        "14" -> UnimplementedInstruction(state)
        "15" -> UnimplementedInstruction(state)
        "16" -> UnimplementedInstruction(state)
        "17" -> UnimplementedInstruction(state)
        "18" -> UnimplementedInstruction(state)
        "19" -> {                                   //DAD   D
            val hl:UInt = (state.h.toUInt().shl(8).or(state.l.toUInt()))
            val de:UInt = (state.d.toUInt().shl(8).or(state.e.toUInt()))
            val res = hl + de
            state.h = (res.and(xFF00).shr(8)).toUByte()
            state.l = res.and(xFF.toUInt()).toUByte()
            if(res>xFFFF){
                state.cc.cy = 1u
            }else{
                state.cc.cy = 0u
            }
        }
        "1A" -> {
            state.a = state.memory[state.offsetDE().toInt()]
        }
        "1B" -> UnimplementedInstruction(state)
        "1C" -> UnimplementedInstruction(state)
        "1D" -> UnimplementedInstruction(state)
        "1E" -> UnimplementedInstruction(state)
        "1F" -> UnimplementedInstruction(state)

        "20" -> UnimplementedInstruction(state)
        "21" -> {
            state.l = state.memory[state.pc.toInt()+1]
            state.h = state.memory[state.pc.toInt()+2]
            state.pc= (state.pc+2u).toUShort()
        }
        "22" -> UnimplementedInstruction(state)
        "23" -> {                           //INX   H
            state.l++
            if (state.l.toInt() == 0){
                state.h++
            }
        }
        "24" -> UnimplementedInstruction(state)
        "25" -> UnimplementedInstruction(state)
        "26" -> {                                   //MVI   H,byte
            state.h = state.memory[state.pc.toInt()+1]
            state.pc++
        }
        "27" -> UnimplementedInstruction(state)
        "28" -> UnimplementedInstruction(state)
        "29" -> {                                   //DAD   HL
            val hl:UInt = (state.h.toUInt().shl(8).or(state.l.toUInt()))
            val res:UInt = hl + hl
            state.h = (res.and(xFF00).shr(8)).toUByte()
            state.l = res.and(xFF.toUInt()).toUByte()
            if(res>xFFFF){
                state.cc.cy=1u
            }else{
                state.cc.cy=0u
            }

        }
        "2A" -> UnimplementedInstruction(state)
        "2B" -> UnimplementedInstruction(state)
        "2C" -> UnimplementedInstruction(state)
        "2D" -> UnimplementedInstruction(state)
        "2E" -> UnimplementedInstruction(state)
        "2F" -> UnimplementedInstruction(state)

        "30" -> UnimplementedInstruction(state)
        "31" -> {
            state.setSP()
            state.pc++
            state.pc++
        }
        "32" -> {                                   //STA   byte
            val offset = (state.memory[state.pc.toInt()+2].toInt()).shl(8)
                    .or(state.memory[state.pc.toInt()+1].toInt())
            state.memory[offset] = state.a
            state.pc = (state.pc +2u).toUShort()
        }
        "33" -> UnimplementedInstruction(state)
        "34" -> UnimplementedInstruction(state)
        "35" -> UnimplementedInstruction(state)
        "36" -> {                                   //MVI   M,byte
            val offset = state.h.toUInt().shl(8).or(state.l.toUInt())
            state.memory[offset.toInt()] = state.memory[state.pc.toInt()+1]
            state.pc++
        }
        "37" -> UnimplementedInstruction(state)
        "38" -> UnimplementedInstruction(state)
        "39" -> UnimplementedInstruction(state)
        "3A" -> {                                   //LDA   byte
            val offset = (state.memory[state.pc.toInt()+2].toInt()).shl(8)
                          .or(state.memory[state.pc.toInt()+1].toInt())
            state.a = state.memory[offset]
            state.pc = (state.pc +2u).toUShort()
        }
        "3B" -> UnimplementedInstruction(state)
        "3C" -> UnimplementedInstruction(state)
        "3D" -> UnimplementedInstruction(state)
        "3E" -> {                                   //MVI   A,byte
            state.a = state.memory[state.pc.toInt()+1]
            state.pc++

        }
        "3F" -> UnimplementedInstruction(state)

        "40" -> UnimplementedInstruction(state)
        "41" -> UnimplementedInstruction(state)
        "42" -> UnimplementedInstruction(state)
        "43" -> UnimplementedInstruction(state)
        "44" -> UnimplementedInstruction(state)
        "45" -> UnimplementedInstruction(state)
        "46" -> UnimplementedInstruction(state)
        "47" -> UnimplementedInstruction(state)
        "48" -> UnimplementedInstruction(state)
        "49" -> UnimplementedInstruction(state)
        "4A" -> UnimplementedInstruction(state)
        "4B" -> UnimplementedInstruction(state)
        "4C" -> UnimplementedInstruction(state)
        "4D" -> UnimplementedInstruction(state)
        "4E" -> UnimplementedInstruction(state)
        "4F" -> UnimplementedInstruction(state)

        "50" -> UnimplementedInstruction(state)
        "51" -> UnimplementedInstruction(state)
        "52" -> UnimplementedInstruction(state)
        "53" -> UnimplementedInstruction(state)
        "54" -> UnimplementedInstruction(state)
        "55" -> UnimplementedInstruction(state)
        "56" -> {                                   //MOV   D,M
            state.d = state.memory[state.offsetHL().toInt()]
        }
        "57" -> UnimplementedInstruction(state)
        "58" -> UnimplementedInstruction(state)
        "59" -> UnimplementedInstruction(state)
        "5A" -> UnimplementedInstruction(state)
        "5B" -> UnimplementedInstruction(state)
        "5C" -> UnimplementedInstruction(state)
        "5D" -> UnimplementedInstruction(state)
        "5E" -> {                                   //MOV   E,M
            state.e = state.memory[state.offsetHL().toInt()]
        }
        "5F" -> UnimplementedInstruction(state)

        "60" -> UnimplementedInstruction(state)
        "61" -> UnimplementedInstruction(state)
        "62" -> UnimplementedInstruction(state)
        "63" -> UnimplementedInstruction(state)
        "64" -> UnimplementedInstruction(state)
        "65" -> UnimplementedInstruction(state)
        "66" -> {                                   //MOV   H,M
            state.h = state.memory[state.offsetHL().toInt()]
        }
        "67" -> UnimplementedInstruction(state)
        "68" -> UnimplementedInstruction(state)
        "69" -> UnimplementedInstruction(state)
        "6A" -> UnimplementedInstruction(state)
        "6B" -> UnimplementedInstruction(state)
        "6C" -> UnimplementedInstruction(state)
        "6D" -> UnimplementedInstruction(state)
        "6E" -> UnimplementedInstruction(state)
        "6F" -> {                                   //MOV   L,A
            state.l = state.a
        }

        "70" -> UnimplementedInstruction(state)
        "71" -> UnimplementedInstruction(state)
        "72" -> UnimplementedInstruction(state)
        "73" -> UnimplementedInstruction(state)
        "74" -> UnimplementedInstruction(state)
        "75" -> UnimplementedInstruction(state)
        "76" -> UnimplementedInstruction(state)
        "77" -> {
            state.memory[state.offsetHL().toInt()] = state.a    //MOV   M,A
        }
        "78" -> UnimplementedInstruction(state)
        "79" -> UnimplementedInstruction(state)
        "7A" -> {                                   //MOV   D,A
            state.a = state.d
        }
        "7B" -> {                                   //MOV   E,A
            state.a = state.e
        }
        "7C" ->{                                    //MOV   A,H
            state.a = state.h
        }
        "7D" -> UnimplementedInstruction(state)
        "7E" -> {                                   //MOV   A,M
            state.a = state.memory[state.offsetHL().toInt()]
        }
        "7F" -> UnimplementedInstruction(state)

        "80" -> UnimplementedInstruction(state)
        "81" -> UnimplementedInstruction(state)
        "82" -> UnimplementedInstruction(state)
        "83" -> UnimplementedInstruction(state)
        "84" -> UnimplementedInstruction(state)
        "85" -> UnimplementedInstruction(state)
        "86" -> UnimplementedInstruction(state)
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

        "A0" -> UnimplementedInstruction(state)
        "A1" -> UnimplementedInstruction(state)
        "A2" -> UnimplementedInstruction(state)
        "A3" -> UnimplementedInstruction(state)
        "A4" -> UnimplementedInstruction(state)
        "A5" -> UnimplementedInstruction(state)
        "A6" -> UnimplementedInstruction(state)
        "A7" -> {                                   //ANA   A
            state.a = state.a.and(state.a)
            state.LogicFlagsA()
        }
        "A8" -> UnimplementedInstruction(state)
        "A9" -> UnimplementedInstruction(state)
        "AA" -> UnimplementedInstruction(state)
        "AB" -> UnimplementedInstruction(state)
        "AC" -> UnimplementedInstruction(state)
        "AD" -> UnimplementedInstruction(state)
        "AE" -> UnimplementedInstruction(state)
        "AF" ->{                                    //XRA   A
            state.a = state.a.xor(state.a)
            state.LogicFlagsA()
        }

        "B0" -> UnimplementedInstruction(state)
        "B1" -> UnimplementedInstruction(state)
        "B2" -> UnimplementedInstruction(state)
        "B3" -> UnimplementedInstruction(state)
        "B4" -> UnimplementedInstruction(state)
        "B5" -> UnimplementedInstruction(state)
        "B6" -> UnimplementedInstruction(state)
        "B7" -> UnimplementedInstruction(state)
        "B8" -> UnimplementedInstruction(state)
        "B9" -> UnimplementedInstruction(state)
        "BA" -> UnimplementedInstruction(state)
        "BB" -> UnimplementedInstruction(state)
        "BC" -> UnimplementedInstruction(state)
        "BD" -> UnimplementedInstruction(state)
        "BE" -> UnimplementedInstruction(state)
        "BF" -> UnimplementedInstruction(state)

        "C0" -> UnimplementedInstruction(state)
        "C1" -> {                                   //POP   B
            state.c = state.memory[state.sp.toInt()]
            state.b = state.memory[state.sp.toInt()+1]
            state.sp = (state.sp + 2u).toUShort()
        }
        "C2" -> {                                   //JNZ   address
            if(state.cc.z == 0u){
                state.setPC()
            }else{
                state.pc = (state.pc + 2u).toUShort()
            }
        }
        "C3" -> {
            state.setPC()
        }
        "C4" -> UnimplementedInstruction(state)
        "C5" -> {                                   //PUSH  B
            state.memory[state.sp.toInt()-1] = state.b
            state.memory[state.sp.toInt()-2] = state.c
            state.sp = (state.sp-2u).toUShort()
        }
        "C6" -> {                                   //ADI   byte
            val res = (state.a.toUShort() + state.memory[state.pc.toInt()].toUShort()).toUShort()
            state.setZero(res)
            state.setSign(res)
            state.setCarry(res)
            //TODO implement parity
            state.a = res.toUByte()
            state.pc++
        }
        "C7" -> UnimplementedInstruction(state)
        "C8" -> UnimplementedInstruction(state)
        "C9" -> {                                   //RET
            val offset = (state.memory[state.sp.toInt()].toUInt()).or(
                                                state.memory[state.sp.toInt()+1].toUInt().shl(8))
            state.pc = offset.toUShort()
            state.sp = (state.sp + 2u).toUShort()
        }
        "CA" -> UnimplementedInstruction(state)
        "CB" -> UnimplementedInstruction(state)
        "CC" -> UnimplementedInstruction(state)
        "CD" -> {                                   //CALL address
            val ret:UInt = state.pc+2u
            state.memory[state.sp.toInt()-1] = ret.shr(8).and(xFF.toUInt()).toUByte()
            state.memory[state.sp.toInt()-2] = ret.and(xFF.toUInt()).toUByte()
            state.sp = (state.sp - 2u).toUShort()
            state.setPC()
        }
        "CE" -> UnimplementedInstruction(state)
        "CF" -> UnimplementedInstruction(state)

        "D0" -> UnimplementedInstruction(state)
        "D1" -> {                                   //POP   D
            state.e = state.memory[state.sp.toInt()]
            state.d = state.memory[state.sp.toInt()+1]
            state.sp = (state.sp + 2u).toUShort()
        }
        "D2" -> UnimplementedInstruction(state)
        "D3" -> {                                   //PUSH  D
            //not doing anything yet
            state.pc++
        }
        "D4" -> UnimplementedInstruction(state)
        "D5" -> {                                   //PUSH  D
            state.memory[state.sp.toInt() - 1] = state.d
            state.memory[state.sp.toInt() - 2] = state.e
            state.sp = (state.sp - 2u).toUShort()
        }
        "D6" -> UnimplementedInstruction(state)
        "D7" -> UnimplementedInstruction(state)
        "D8" -> UnimplementedInstruction(state)
        "D9" -> UnimplementedInstruction(state)
        "DA" -> UnimplementedInstruction(state)
        "DB" -> UnimplementedInstruction(state)
        "DC" -> UnimplementedInstruction(state)
        "DD" -> UnimplementedInstruction(state)
        "DE" -> UnimplementedInstruction(state)
        "DF" -> UnimplementedInstruction(state)

        "E0" -> UnimplementedInstruction(state)
        "E1" -> {                                   //POP   H
            state.l = state.memory[state.sp.toInt()]
            state.h = state.memory[state.sp.toInt()+1]
            state.sp = (state.sp + 2u).toUShort()
        }
        "E2" -> UnimplementedInstruction(state)
        "E3" -> UnimplementedInstruction(state)
        "E4" -> UnimplementedInstruction(state)
        "E5" -> {                                   //PUSH  H
            state.memory[state.sp.toInt()-1] = state.h
            state.memory[state.sp.toInt()-2] = state.l
            state.sp = (state.sp-2u).toUShort()
        }
        "E6" -> {                                   //ANI   byte
            state.a = (state.a.and(state.memory[state.pc.toInt()]))
            state.LogicFlagsA()
            state.pc++
        }
        "E7" -> UnimplementedInstruction(state)
        "E8" -> UnimplementedInstruction(state)
        "E9" -> UnimplementedInstruction(state)
        "EA" -> UnimplementedInstruction(state)
        "EB" -> {                                   //XCHG
            val save1 = state.d
            val save2 = state.e
            state.d = state.h
            state.e = state.l
            state.h = save1
            state.l = save2
        }
        "EC" -> UnimplementedInstruction(state)
        "ED" -> UnimplementedInstruction(state)
        "EE" -> UnimplementedInstruction(state)
        "EF" -> UnimplementedInstruction(state)

        "F0" -> UnimplementedInstruction(state)
        "F1" -> {                                   //POP psw
            state.a = state.memory[state.sp.toInt()+1]
            val psw = state.memory[state.sp.toInt()]

            if(psw.and(x01) == x01){
                state.cc.z = 1u
            }else{
                state.cc.z = 0u
            }

            if(psw.and(x02) == x02){
                state.cc.s = 1u
            }else{
                state.cc.s = 0u
            }

            if(psw.and(x04) == x04){
                state.cc.p = 1u
            }else{
                state.cc.p = 0u
            }

            if(psw.and(x08) == x05){
                state.cc.cy = 1u
            }else{
                state.cc.cy = 0u
            }

            if(psw.and(x10) == x10){
                state.cc.ac = 1u
            }else{
                state.cc.ac = 0u
            }
            state.sp = (state.sp + 2u).toUShort()
        }
        "F2" -> UnimplementedInstruction(state)
        "F3" -> UnimplementedInstruction(state)
        "F4" -> UnimplementedInstruction(state)
        "F5" -> {
            state.memory[state.sp.toInt()-1] = state.a
            val psw:UByte = (state.cc.z
                    .or(state.cc.s).shl(1)
                    .or(state.cc.p).shl(2)
                    .or(state.cc.cy).shl(3)
                    .or(state.cc.ac).shl(4))
                    .toUByte()
            state.memory[state.sp.toInt()-2] = psw
            state.sp = (state.sp -2u).toUShort()
        }
        "F6" -> UnimplementedInstruction(state)
        "F7" -> UnimplementedInstruction(state)
        "F8" -> UnimplementedInstruction(state)
        "F9" -> UnimplementedInstruction(state)
        "FA" -> UnimplementedInstruction(state)
        "FB" -> {                                   //EI
            state.int_enable = 1u
        }
        "FC" -> UnimplementedInstruction(state)
        "FD" -> UnimplementedInstruction(state)
        "FE" -> {
            val value = state.a - state.memory[state.pc.toInt()+1]
            state.setZero(value.toUByte())
            state.setSign(value.toUByte())
            //TODO implement parity
            state.setCarry(value.toUByte())
            state.pc++

        }
        "FF" -> UnimplementedInstruction(state)
    }


    state.pc++

}

