@kotlin.ExperimentalUnsignedTypes
fun Emulate8080Op(state:Registers):Int {
    var opcode  =  state.memory[state.pc.toInt()].toInt()
    //println(String.format("%02x\t%04x", opcode, state.memory[state.pc.toInt()].toInt()))
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
            val bc: UInt = (state.b.toUInt().shl(8).or(state.c.toUInt()))
            val res = hl + bc
            state.h = (res.and(0xFF00u).shr(8)).toUByte()
            state.l = res.and(0x00FFu.toUInt()).toUByte()
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
            if (state.l == 0u.toUByte()) {
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
        0x2C -> {
            state.l++
            state.flagsZSP(state.l)
            state.pc++
        }
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
        0x41 -> {
            state.b = state.c
            state.pc++
        }
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
        0x71 -> {
            state.writeToHL(state.c)
            state.pc++
        }
        0x72 -> UnimplementedInstruction(state)
        0x73 -> UnimplementedInstruction(state)
        0x74 -> UnimplementedInstruction(state)
        0x75 -> UnimplementedInstruction(state)
        0x76 -> UnimplementedInstruction(state)
        0x77 -> {                                   //MOV   M,A
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
        0x97 -> {
            val res = (state.a-state.a).toUShort()
            state.ArithFlags(res)
            state.a = res.toUByte()
            state.pc++
        }
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
            state.WriteMem(state.sp.toInt() - 1, (ret.shr(8)).toUByte())
            state.WriteMem(state.sp.toInt() - 2, ret.and(0xffu).toUByte())
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

            if (psw.and(0x08u) == 0x08u.toUByte()) {
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
            state.memory[state.sp.toInt() - 1] = state.a
            val psw: UByte = (state.cc.z
                    .or(state.cc.s.shl(1))
                    .or(state.cc.p.shl(2))
                    .or(state.cc.cy.shl(3))
                    .or(state.cc.ac.shl(4)))
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