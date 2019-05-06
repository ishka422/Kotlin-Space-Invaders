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
    //var lastInterupt = time()

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
       cc.z = if(value.and(0xFFu).compareTo(0u) == 0) 1u else 0u
    }

    fun setZero(value:UByte){
        cc.z = if(value.and(0xFFu).compareTo(0u) == 0) 1u else 0u
    }

    fun setSign(value:UShort){
       cc.s = if(value.and(0x80u).compareTo(0u) == 0) 0u else 1u
    }
    fun setSign(value:UByte){
       cc.s = if(value.and(0x80u).compareTo(0u) == 0) 0u else 1u
    }

    fun setCarry(value:UShort){
       cc.cy = if(value > 0xFFu) 1u else 0u
    }

    fun setCarry(value:UByte){
        cc.cy = if(value > 0xFFu) 1u else 0u
    }
    fun setPC(){

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
        setCarry(res)
        setZero(res)
        setSign(res)
        //TODO implemet parity

    }
    fun Push(high:UByte, low:UByte){
        WriteMem(sp.toInt()-1, high)
        WriteMem(sp.toInt()-2, low)
        sp = sp-2u
    }
    fun WriteMem(address:Int, value:UByte){

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
        val low = (pc.and(0x00FFu)).toUByte()
        Push(high,low)
        pc = (8*interupt_num).toUInt()
        int_enable = 0
    }

//    fun time():Long{
//        return System.currentTimeMillis()
//    }
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