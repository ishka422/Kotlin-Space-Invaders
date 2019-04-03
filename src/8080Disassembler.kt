import java.io.File
import java.util.*
import kotlin.system.exitProcess

const val xFF:UShort = 255u
const val x80:UShort = 128u


class ConditionCodes(){
    var z   = BitSet(1)
    var s   = BitSet(1)
    var p   = BitSet(1)
    var cy  = BitSet(1)
    var ac  = BitSet(1)
    var pad = BitSet(3)

}

class Register(var value:UByte)

class Registers(val memSize:Int){

    var a = Register(0u)
    var b = Register(0u)
    var c = Register(0u)
    var d = Register(0u)
    var e = Register(0u)
    var h = Register(0u)
    var l = Register(0u)
    var sp:UShort = 0u
    var pc:UShort = 0u
    var memory:ByteArray = ByteArray(memSize)       //memory for machine, needs to be an array, size should be 0x2000
    var cc = ConditionCodes()
    var int_enable:UByte = 0u

    fun setZero(value:UShort){
        //condense this
        if(value.and(xFF).compareTo(0u) == 0){
            cc.z.set(0,1)
        }else{
            cc.z.set(0,0)
        }
    }

    fun setSign(value:UShort){
        cc.s.set(0,value.and(x80).compareTo(0u))
        cc.s.flip(0)
    }

    fun setCarry(value:UShort){
        if(value > xFF){
            cc.cy.set(0,1)
        }else{
            cc.cy.set(0,0)
        }
    }
}


fun main() {


    val bytes= File("invaders").readBytes()
    var emulate = Registers(bytes.size)
    emulate.memory = bytes
    emulate.sp = 128u
    while(emulate.pc.toInt()<bytes.size){
        Emulate8080Op(emulate)
    }

}
fun UnimplementedInstruction(state:Registers){
    state.pc = (state.pc.minus(1u)).toUShort()
    println(String.format("Error Unimplemented instruction:\t%02x\n",state.memory[state.pc.dec().toInt()]))
    exitProcess(2)
}
fun Emulate8080Op(state:Registers){
    var opcode = String.format("%02x",state.memory[state.pc.toInt()])

    when(opcode){
        "00" -> state.a = state.a   // NOP
        "01" -> UnimplementedInstruction(state)
        "02" -> UnimplementedInstruction(state)
        "03" -> UnimplementedInstruction(state)
        "04" -> UnimplementedInstruction(state)
        "05" -> UnimplementedInstruction(state)
        "06" -> UnimplementedInstruction(state)
        "07" -> UnimplementedInstruction(state)
        "08" -> UnimplementedInstruction(state)
        "09" -> UnimplementedInstruction(state)
        "0A" -> UnimplementedInstruction(state)
        "0B" -> UnimplementedInstruction(state)
        "0C" -> UnimplementedInstruction(state)
        "0D" -> UnimplementedInstruction(state)
        "0E" -> UnimplementedInstruction(state)
        "0F" -> UnimplementedInstruction(state)

        "10" -> UnimplementedInstruction(state)
        "11" -> UnimplementedInstruction(state)
        "12" -> UnimplementedInstruction(state)
        "13" -> UnimplementedInstruction(state)
        "14" -> UnimplementedInstruction(state)
        "15" -> UnimplementedInstruction(state)
        "16" -> UnimplementedInstruction(state)
        "17" -> UnimplementedInstruction(state)
        "18" -> UnimplementedInstruction(state)
        "19" -> UnimplementedInstruction(state)
        "1A" -> UnimplementedInstruction(state)
        "1B" -> UnimplementedInstruction(state)
        "1C" -> UnimplementedInstruction(state)
        "1D" -> UnimplementedInstruction(state)
        "1E" -> UnimplementedInstruction(state)
        "1F" -> UnimplementedInstruction(state)

        "20" -> UnimplementedInstruction(state)
        "21" -> UnimplementedInstruction(state)
        "22" -> UnimplementedInstruction(state)
        "23" -> UnimplementedInstruction(state)
        "24" -> UnimplementedInstruction(state)
        "25" -> UnimplementedInstruction(state)
        "26" -> UnimplementedInstruction(state)
        "27" -> UnimplementedInstruction(state)
        "28" -> UnimplementedInstruction(state)
        "29" -> UnimplementedInstruction(state)
        "2A" -> UnimplementedInstruction(state)
        "2B" -> UnimplementedInstruction(state)
        "2C" -> UnimplementedInstruction(state)
        "2D" -> UnimplementedInstruction(state)
        "2E" -> UnimplementedInstruction(state)
        "2F" -> UnimplementedInstruction(state)

        "30" -> UnimplementedInstruction(state)
        "31" -> UnimplementedInstruction(state)
        "32" -> UnimplementedInstruction(state)
        "33" -> UnimplementedInstruction(state)
        "34" -> UnimplementedInstruction(state)
        "35" -> UnimplementedInstruction(state)
        "36" -> UnimplementedInstruction(state)
        "37" -> UnimplementedInstruction(state)
        "38" -> UnimplementedInstruction(state)
        "39" -> UnimplementedInstruction(state)
        "3A" -> UnimplementedInstruction(state)
        "3B" -> UnimplementedInstruction(state)
        "3C" -> UnimplementedInstruction(state)
        "3D" -> UnimplementedInstruction(state)
        "3E" -> UnimplementedInstruction(state)
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
        "56" -> UnimplementedInstruction(state)
        "57" -> UnimplementedInstruction(state)
        "58" -> UnimplementedInstruction(state)
        "59" -> UnimplementedInstruction(state)
        "5A" -> UnimplementedInstruction(state)
        "5B" -> UnimplementedInstruction(state)
        "5C" -> UnimplementedInstruction(state)
        "5D" -> UnimplementedInstruction(state)
        "5E" -> UnimplementedInstruction(state)
        "5F" -> UnimplementedInstruction(state)

        "60" -> UnimplementedInstruction(state)
        "61" -> UnimplementedInstruction(state)
        "62" -> UnimplementedInstruction(state)
        "63" -> UnimplementedInstruction(state)
        "64" -> UnimplementedInstruction(state)
        "65" -> UnimplementedInstruction(state)
        "66" -> UnimplementedInstruction(state)
        "67" -> UnimplementedInstruction(state)
        "68" -> UnimplementedInstruction(state)
        "69" -> UnimplementedInstruction(state)
        "6A" -> UnimplementedInstruction(state)
        "6B" -> UnimplementedInstruction(state)
        "6C" -> UnimplementedInstruction(state)
        "6D" -> UnimplementedInstruction(state)
        "6E" -> UnimplementedInstruction(state)
        "6F" -> UnimplementedInstruction(state)

        "70" -> UnimplementedInstruction(state)
        "71" -> UnimplementedInstruction(state)
        "72" -> UnimplementedInstruction(state)
        "73" -> UnimplementedInstruction(state)
        "74" -> UnimplementedInstruction(state)
        "75" -> UnimplementedInstruction(state)
        "76" -> UnimplementedInstruction(state)
        "77" -> UnimplementedInstruction(state)
        "78" -> UnimplementedInstruction(state)
        "79" -> UnimplementedInstruction(state)
        "7A" -> UnimplementedInstruction(state)
        "7B" -> UnimplementedInstruction(state)
        "7C" -> UnimplementedInstruction(state)
        "7D" -> UnimplementedInstruction(state)
        "7E" -> UnimplementedInstruction(state)
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
        "A7" -> UnimplementedInstruction(state)
        "A8" -> UnimplementedInstruction(state)
        "A9" -> UnimplementedInstruction(state)
        "AA" -> UnimplementedInstruction(state)
        "AB" -> UnimplementedInstruction(state)
        "AC" -> UnimplementedInstruction(state)
        "AD" -> UnimplementedInstruction(state)
        "AE" -> UnimplementedInstruction(state)
        "AF" -> UnimplementedInstruction(state)

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
        "C1" -> UnimplementedInstruction(state)
        "C2" -> UnimplementedInstruction(state)
        "C3" -> UnimplementedInstruction(state)
        "C4" -> UnimplementedInstruction(state)
        "C5" -> UnimplementedInstruction(state)
        "C6" -> UnimplementedInstruction(state)
        "C7" -> UnimplementedInstruction(state)
        "C8" -> UnimplementedInstruction(state)
        "C9" -> UnimplementedInstruction(state)
        "CA" -> UnimplementedInstruction(state)
        "CB" -> UnimplementedInstruction(state)
        "CC" -> UnimplementedInstruction(state)
        "CD" -> UnimplementedInstruction(state)
        "CE" -> UnimplementedInstruction(state)
        "CF" -> UnimplementedInstruction(state)

        "D0" -> UnimplementedInstruction(state)
        "D1" -> UnimplementedInstruction(state)
        "D2" -> UnimplementedInstruction(state)
        "D3" -> UnimplementedInstruction(state)
        "D4" -> UnimplementedInstruction(state)
        "D5" -> UnimplementedInstruction(state)
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
        "E1" -> UnimplementedInstruction(state)
        "E2" -> UnimplementedInstruction(state)
        "E3" -> UnimplementedInstruction(state)
        "E4" -> UnimplementedInstruction(state)
        "E5" -> UnimplementedInstruction(state)
        "E6" -> UnimplementedInstruction(state)
        "E7" -> UnimplementedInstruction(state)
        "E8" -> UnimplementedInstruction(state)
        "E9" -> UnimplementedInstruction(state)
        "EA" -> UnimplementedInstruction(state)
        "EB" -> UnimplementedInstruction(state)
        "EC" -> UnimplementedInstruction(state)
        "ED" -> UnimplementedInstruction(state)
        "EE" -> UnimplementedInstruction(state)
        "EF" -> UnimplementedInstruction(state)

        "F0" -> UnimplementedInstruction(state)
        "F1" -> UnimplementedInstruction(state)
        "F2" -> UnimplementedInstruction(state)
        "F3" -> UnimplementedInstruction(state)
        "F4" -> UnimplementedInstruction(state)
        "F5" -> UnimplementedInstruction(state)
        "F6" -> UnimplementedInstruction(state)
        "F7" -> UnimplementedInstruction(state)
        "F8" -> UnimplementedInstruction(state)
        "F9" -> UnimplementedInstruction(state)
        "FA" -> UnimplementedInstruction(state)
        "FB" -> UnimplementedInstruction(state)
        "FC" -> UnimplementedInstruction(state)
        "FD" -> UnimplementedInstruction(state)
        "FE" -> UnimplementedInstruction(state)
        "FF" -> UnimplementedInstruction(state)
    }

    state.pc = (state.pc.plus(1u)).toUShort()
    println(opcode + "  "+ state.pc )
}
