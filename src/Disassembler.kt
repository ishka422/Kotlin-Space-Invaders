fun disassemble(bytes:UByteArray){
    var i = 0
    var st = " "
    while (i < bytes.size) {
        st = String.format("%02X", bytes[i].toByte())
        print(String.format("%04X    ", i))
        when (st) {
            "00" -> {
                println("00 NOP")
                i++
            }
            "01" -> {
                println(String.format("01  LXI, B: %02X %02X", bytes[i+2].toByte(), bytes[i+1].toByte()))
                i++
            }
            "02" -> {
                println(String.format("02  STAX B"))
                i++

            }
            "03" -> {
                println(String.format("03  INX B"))
                i++

            }
            "04" -> {
                println(String.format("04  INR B"))
                i++

            }
            "05" -> {
                println(String.format("05  DCR B"))
                i++

            }
            "06" -> {
                println(String.format("06  MVI B <- %02X", bytes[i+1].toByte()))
                i++

            }
            "07" -> {
                println(String.format("07  RLC"))
                i++

            }
            "08" -> {
                println(String.format("08  reserved"))
                i++

            }
            "09" -> {
                println(String.format("09  DAD B"))
                i++

            }
            "0A" -> {
                println(String.format("0a  LDAX B"))
                i++

            }
            "0B" -> {
                println(String.format("0B  DCX B"))
                i++

            }
            "0C" -> {
                println(String.format("0c  INR C"))
                i++

            }
            "0D" -> {
                println(String.format("0d DCR C"))
                i++

            }
            "0E" -> {
                println(String.format("0e MVI      C  <-  %02X", bytes[i+1].toByte()))
                i++

            }
            "0F" -> {
                println("0F  RRC")
                i++

            }


            "10" -> {
                println(String.format("10   -----"))
                i ++

            }
            "11" -> {
                println(String.format("11 LXI  D <-  %02X  E <-  %02X", bytes[i+2].toByte(), bytes[i+1].toByte()))
                i++
            }
            "12" -> {
                println(String.format("12 STAX D"))
                i++

            }
            "13" -> {
                println(String.format("13 INX D"))
                i++

            }
            "14" -> {
                println(String.format("14 INR D"))
                i++

            }
            "15" -> {
                println(String.format("15 DCR D"))
                i++

            }
            "16" -> {
                println(String.format("16  MVI  D  <- %02X", bytes[i+1].toByte()))
                i++

            }
            "17" -> {
                println(String.format("17 RAL"))
                i++

            }
            "18" -> {
                println(String.format("18 ----"))
                i ++

            }
            "19" -> {
                println(String.format("19 DAD D"))
                i++

            }
            "1A" -> {
                println(String.format("1a LDAX D"))
                i++

            }
            "1B" -> {
                println(String.format("1b DCX D"))
                i++

            }
            "1C" -> {
                println(String.format("1c INR E"))
                i++

            }
            "1D" -> {
                println(String.format("1d DCR E"))
                i++

            }
            "1E" -> {
                println(String.format("1e MVI  E  <- %02X", bytes[i+1].toByte()))
                i++

            }
            "1F" -> {
                println(String.format("1f RAR"))
                i++

            }

            "20" -> {
                println(String.format("20 RIM"))
                i++

            }
            "21" -> {
                println(String.format("21 LXI    H  <-  %02X L  <-  %02X", bytes[i+2].toByte(), bytes[i+1].toByte()))
                i++
            }
            "22" -> {
                println(String.format("22 DHLD     (adr)  <-  L, (adr)  <-  H", bytes[i+1].toByte(), bytes [i+2].toByte()))
                i++

            }
            "23" -> {
                println(String.format("23 INX H"))
                i++

            }
            "24" -> {
                println(String.format("24 INR H"))
                i++

            }
            "25" -> {
                println(String.format("25 DCR H"))
                i++

            }
            "26" -> {
                println(String.format("26 MVI      H  <-  %02X", bytes[i+1].toByte()))
                i++

            }
            "27" -> {
                println(String.format("27 DAA"))
                i++

            }
            "28" -> {
                println(String.format("28 This should not be called"))
                i ++
            }
            "29" -> {
                println(String.format("29 DAD H"))
                i++

            }
            "2A" -> {
                println(String.format("2a LHLD adr    L  <- (adr)  H  <- (adr)"))
                i++

            }
            "2B" -> {
                println(String.format("2b DCX H"))
                i++

            }
            "2C" -> {
                println(String.format("2c INR L"))
                i++

            }
            "2D" -> {
                println(String.format("2d DCR L"))
                i++

            }
            "2E" -> {
                println(String.format("2e MVI     L  <-  %02X", bytes[i+1].toByte()))
                i++

            }
            "2F" -> {
                println(String.format("2f CMA"))
                i++

            }

            "30" -> {
                println(String.format("30 SIM"))
                i++

            }
            "31" -> {
                println(String.format("31 LXI      SP.hi  <- %02X  SP.lo  <- %02X", bytes[i+2].toByte(), bytes[i+1].toByte()))
                i++
            }
            "32" -> {
                println(String.format("32 STA    a16, %02X %02X", bytes[i+1].toByte(), bytes [i+2].toByte()))
                i++

            }
            "33" -> {
                println(String.format("33 INX  SP"))
                i++

            }
            "34" -> {
                println(String.format("34 INR  M"))
                i++

            }
            "35" -> {
                println("35 DCR    M")
                i++

            }
            "36" -> {
                println(String.format("36 MVI      M  <-  %02X", bytes[i+1].toByte()))
                i++

            }
            "37" -> {
                println(String.format("37 STC"))
                i ++

            }
            "38" -> {
                println(String.format("38 This should not be called"))
                i ++

            }
            "39" -> {
                println(String.format("DAD  SP"))
                i ++

            }
            "3A" -> {
                println(String.format("LDA  adr"))
                i++

            }
            "3B" -> {
                println(String.format("DCX SP"))
                i++

            }
            "3C" -> {
                println(String.format("INR A"))
                i ++

            }
            "3D" -> {
                println(String.format("DCR A"))
                i ++

            }
            "3E" -> {
                println(String.format("MVI    A, %02X", bytes[i+1].toByte()))
                i++

            }
            "3F" -> {
                println(String.format("CMC"))
                i ++

            }

            "40" -> {
                println(String.format("MOV  B  <-  B"))
                i++

            }
            "41" -> {
                println(String.format("MOV  B  <-  C"))
                i++
            }
            "42" -> {
                println(String.format("MOV  B  <-  D"))
                i ++

            }
            "43" -> {
                println(String.format("MOV  B  <-  E"))
                i ++

            }
            "44" -> {
                println(String.format("MOV  B  <-  H"))
                i ++

            }
            "45" -> {
                println(String.format("MOV  B  <-  L"))
                i++

            }
            "46" -> {
                println(String.format("MOV  B  <-  M"))
                i++

            }
            "47" -> {
                println(String.format("MOV  B  <-  A"))
                i ++

            }
            "48" -> {
                println(String.format("MOV  C  <-  B"))
                i++

            }
            "49" -> {
                println(String.format("MOV  C  <-  C"))
                i ++

            }
            "4A" -> {
                println(String.format("MOV  C  <-  D"))
                i ++

            }
            "4B" -> {
                println(String.format("MOV  C  <-  E"))
                i ++

            }
            "4C" -> {
                println(String.format("MOV  C  <-  H"))
                i ++

            }
            "4D" -> {
                println(String.format("MOV  C  <-  L"))
                i ++

            }
            "4E" -> {
                println(String.format("MOV  C  <-  M"))
                i++

            }
            "4F" -> {
                println(String.format("MOV  C  <-  A"))
                i ++

            }

            "50" -> {
                println(String.format("MOV  D  <-  B"))
                i ++

            }
            "51" -> {
                println(String.format("MOV  D  <-  C"))
                i++
            }
            "52" -> {
                println(String.format("MOV  D  <-  D"))
                i ++

            }
            "53" -> {
                println(String.format("MOV  D  <-  E"))
                i++

            }
            "54" -> {
                println(String.format("MOV  D  <-  H"))
                i ++

            }
            "55" -> {
                println(String.format("MOV  D  <-  L"))
                i ++

            }
            "56" -> {
                println(String.format("MOV  D  <-  M"))
                i ++

            }
            "57" -> {
                println(String.format("MOV  D  <-  A"))
                i++

            }
            "58" -> {
                println(String.format("MOV  E  <-  B"))
                i ++

            }
            "59" -> {
                println(String.format("MOV  E  <-  C"))
                i ++

            }
            "5A" -> {
                println(String.format("MOV  E  <-  D"))
                i ++

            }
            "5B" -> {
                println(String.format("MOV  E  <-  E"))
                i ++

            }
            "5C" -> {
                println(String.format("MOV  E  <-  H"))
                i ++
            }
            "5D" -> {
                println(String.format("MOV  E  <-  L"))
                i ++

            }
            "5E" -> {
                println(String.format("MOV  E  <-  M"))
                i ++
            }
            "5F" -> {
                println(String.format("MOV  E  <-  A"))
                i ++

            }

            "60" -> {
                println(String.format("MOV  H  <-  B"))
                i ++

            }
            "61" -> {
                println(String.format("MOV  H  <-  C"))
                i++
            }
            "62" -> {
                println(String.format("MOV  H  <-  D"))
                i ++

            }
            "63" -> {
                println(String.format("MOV  H  <-  E"))
                i ++

            }
            "64" -> {
                println(String.format("MOV  H  <-  H"))
                i ++

            }
            "65" -> {
                println(String.format("MOV  H  <-  L"))
                i ++

            }
            "66" -> {
                println(String.format("MOV  H  <-  M"))
                i ++

            }
            "67" -> {
                println(String.format("MOV  H  <-  A"))
                i ++

            }
            "68" -> {
                println(String.format("MOV  L  <-  B"))
                i ++

            }
            "69" -> {
                println(String.format("MOV  L  <-  C"))
                i ++

            }
            "6A" -> {
                println(String.format("MOV  L  <-  D"))
                i ++

            }
            "6B" -> {
                println(String.format("MOV  L  <-  E"))
                i ++

            }
            "6C" -> {
                println(String.format("MOV  L  <-  H"))
                i ++

            }
            "6D" -> {
                println(String.format("MOV  L  <-  L"))
                i ++

            }
            "6E" -> {
                println(String.format("MOV  L  <-  M"))
                i ++

            }
            "6F" -> {
                println(String.format("MOV  L  <-  A"))
                i ++

            }

            "70" -> {
                println(String.format("MOV  M  <-  B"))
                i ++

            }
            "71" -> {
                println(String.format("MOV  M  <-  C"))
                i++
            }
            "72" -> {
                println(String.format("MOV  M  <-  D"))
                i ++
            }
            "73" -> {
                println(String.format("MOV  M  <-  E"))
                i ++

            }
            "74" -> {
                println(String.format("MOV  M  <-  H"))
                i ++

            }
            "75" -> {
                println(String.format("MOV  M  <-  L"))
                i ++

            }
            "76" -> {
                println(String.format("HLT"))
                i ++

            }
            "77" -> {
                println(String.format("MOV  M  <-  A"))
                i ++

            }
            "78" -> {
                println(String.format("MOV  A  <-  B"))
                i ++

            }
            "79" -> {
                println(String.format("MOV  A  <-  C"))
                i ++

            }
            "7A" -> {
                println(String.format("MOV  A  <-  D"))
                i ++

            }
            "7B" -> {
                println(String.format("MOV  A  <-  E"))
                i ++

            }
            "7C" -> {
                println(String.format("MOV  A  <-  H"))
                i ++

            }
            "7D" -> {
                println(String.format("MOV  A  <-  L"))
                i ++

            }
            "7E" -> {
                println(String.format("MOV  A  <-  M"))
                i ++

            }
            "7F" -> {
                println(String.format("MOV  A  <-  A"))
                i ++

            }

            "80" -> {
                println(String.format("ADD B"))
                i ++

            }
            "81" -> {
                println(String.format("ADD C"))
                i++
            }
            "82" -> {
                println(String.format("ADD D"))
                i ++

            }
            "83" -> {
                println(String.format("ADD E"))
                i ++

            }
            "84" -> {
                println(String.format("ADD H"))
                i ++

            }
            "85" -> {
                println(String.format("ADD L"))
                i ++

            }
            "86" -> {
                println(String.format("ADD M"))
                i ++

            }
            "87" -> {
                println(String.format("ADD A"))
                i ++

            }
            "88" -> {
                println(String.format("ADC B"))
                i++

            }
            "89" -> {
                println(String.format("ADC C"))
                i++

            }
            "8A" -> {
                println(String.format("ADC D"))
                i ++

            }
            "8B" -> {
                println(String.format("ADC E"))
                i ++

            }
            "8C" -> {
                println(String.format("ADC H"))
                i ++

            }
            "8D" -> {
                println(String.format("ADC L"))
                i ++

            }
            "8E" -> {
                println(String.format("ADC M"))
                i ++

            }
            "8F" -> {
                println(String.format("ADC A"))
                i ++

            }

            "90" -> {
                println(String.format("SUB B"))
                i ++

            }
            "91" -> {
                println(String.format("SUB C"))
                i++
            }
            "92" -> {
                println(String.format("SUB D"))
                i ++

            }
            "93" -> {
                println(String.format("SUB E"))
                i ++

            }
            "94" -> {
                println(String.format("SUB H"))
                i ++

            }
            "95" -> {
                println(String.format("SUB L"))
                i ++

            }
            "96" -> {
                println(String.format("SUB M"))
                i ++

            }
            "97" -> {
                println(String.format("SUB A"))
                i ++

            }
            "98" -> {
                println(String.format("SBB B"))
                i ++

            }
            "99" -> {
                println(String.format("SBB C"))
                i ++

            }
            "9A" -> {
                println(String.format("SBB D"))
                i ++

            }
            "9B" -> {
                println(String.format("SBB E"))
                i ++

            }
            "9C" -> {
                println(String.format("SBB H"))
                i ++

            }
            "9D" -> {
                println(String.format("SBB L"))
                i ++

            }
            "9E" -> {
                println(String.format("SBB M"))
                i ++

            }
            "9F" -> {
                println(String.format("SBB A"))
                i ++

            }

            "A0" -> {
                println(String.format("ANA B"))
                i ++

            }
            "A1" -> {
                println(String.format("ANA C"))
                i++
            }
            "A2" -> {
                println(String.format("ANA D"))
                i ++

            }
            "A3" -> {
                println(String.format("ANA E"))
                i ++

            }
            "A4" -> {
                println(String.format("ANA H"))
                i ++

            }
            "A5" -> {
                println(String.format("ANA L"))
                i ++

            }
            "A6" -> {
                println(String.format("ANA M"))
                i ++

            }
            "A7" -> {
                println(String.format("ANA A"))
                i ++

            }
            "A8" -> {
                println(String.format("XRA B"))
                i ++

            }
            "A9" -> {
                println(String.format("XRA C"))
                i ++

            }
            "AA" -> {
                println(String.format("XRA D"))
                i ++

            }
            "AB" -> {
                println(String.format("XRA E"))
                i ++

            }
            "AC" -> {
                println(String.format("XRA H"))
                i ++

            }
            "AD" -> {
                println(String.format("XRA L"))
                i ++

            }
            "AE" -> {
                println(String.format("XRA M"))
                i ++

            }
            "AF" -> {
                println(String.format("XRA A"))
                i ++

            }

            "B0" -> {
                println(String.format("ORA B"))
                i ++

            }
            "B1" -> {
                println(String.format("ORA C"))
                i++
            }
            "B2" -> {
                println(String.format("ORA D"))
                i ++

            }
            "B3" -> {
                println(String.format("ORA E"))
                i ++

            }
            "B4" -> {
                println(String.format("ORA H"))
                i ++

            }
            "B5" -> {
                println(String.format("ORA L"))
                i ++

            }
            "B6" -> {
                println(String.format("ORA M"))
                i ++

            }
            "B7" -> {
                println(String.format("ORA A"))
                i ++

            }
            "B8" -> {
                println(String.format("CMP B"))
                i ++

            }
            "B9" -> {
                println(String.format("CMP C"))
                i ++

            }
            "BA" -> {
                println(String.format("CMP D"))
                i ++

            }
            "BB" -> {
                println(String.format("CMP E"))
                i ++

            }
            "BC" -> {
                println(String.format("CMP H"))
                i ++

            }
            "BD" -> {
                println(String.format("CMP L"))
                i ++

            }
            "BE" -> {
                println(String.format("CMP M"))
                i ++

            }
            "BF" -> {
                println(String.format("CMP A"))
                i ++

            }

            "C0" -> {
                println(String.format("RNZ"))
                i++

            }
            "C1" -> {
                println(String.format("POP B"))
                i++
            }
            "C2" -> {
                println(String.format("JNZ %02X %02X", bytes[i+2].toByte(), bytes[i+1].toByte()))
                i++

            }
            "C3" -> {
                println(String.format("JMP  %02X %02X", bytes[i+2].toByte(), bytes[i+1].toByte()))
                i++
            }
            "C4" -> {
                println(String.format("CNZ  %02X %02X", bytes[i+2].toByte(), bytes[i+1].toByte()))
                i++

            }
            "C5" -> {
                println("PUSH B")
                i++

            }
            "C6" -> {
                println(String.format("ADI %02X", bytes[i+1].toByte()))
                i++

            }
            "C7" -> {
                println(String.format("RST 0"))
                i ++

            }
            "C8" -> {
                println(String.format("RZ"))
                i ++

            }
            "C9" -> {
                println(String.format("RET"))
                i ++

            }
            "CA" -> {
                println(String.format("JZ  %02X %02X", bytes[i+2].toByte(), bytes[i+1].toByte()))
                i++

            }
            "CB" -> {
                println(String.format("CB  -----"))
                i++

            }
            "CC" -> {
                println(String.format("CZ %02X %02X", bytes[i+2].toByte(), bytes[i+1].toByte()))
                i++

            }
            "CD" -> {
                println(String.format("CALL     a16, %02X %02X", bytes[i+2].toByte(), bytes[i+1].toByte()))
                i++

            }
            "CE" -> {
                println(String.format("ACI %02X", bytes[i+1].toByte()))
                i++

            }
            "CF" -> {
                println(String.format("RST 1"))
                i ++

            }

            "D0" -> {
                println(String.format("RNC"))
                i ++

            }
            "D1" -> {
                println(String.format("POP D"))
                i++
            }
            "D2" -> {
                println(String.format("JNC %02X %02X", bytes[i+2].toByte(), bytes[i+1].toByte()))
                i++

            }
            "D3" -> {
                println(String.format("OUT D8"))
                i++

            }
            "D4" -> {
                println(String.format("CNC %02X %02X", bytes[i+2].toByte(), bytes[i+1].toByte()))
                i++

            }
            "D5" -> {
                println("PUSH D")
                i++

            }
            "D6" -> {
                println(String.format("SUI %02X", bytes[i+1].toByte()))
                i++

            }
            "D7" -> {
                println(String.format("RST 2"))
                i ++

            }
            "D8" -> {
                println(String.format("RC"))
                i++

            }
            "D9" -> {
                println(String.format("D9  ----"))
                i ++

            }
            "DA" -> {
                println(String.format("JC     a16, %02X %02X", bytes[i+2].toByte(), bytes[i+1].toByte()))
                i++

            }
            "DB" -> {
                println(String.format("IN     d8, %02X", bytes[i+1].toByte()))
                i++

            }
            "DC" -> {
                println(String.format("CC %02X %02X", bytes[i+2].toByte(), bytes[i+1].toByte()))
                i++

            }
            "DD" -> {
                println(String.format("DD  ----"))
                i++
            }
            "DE" -> {
                println(String.format("SBI %02X", bytes[i+1].toByte()))
                i++

            }
            "DF" -> {
                println(String.format("RST 3"))
                i ++

            }


            "E0" -> {
                println(String.format("RPO"))
                i ++

            }
            "E1" -> {
                println(String.format("POP H"))
                i++
            }
            "E2" -> {
                println(String.format("JPO %02X %02X", bytes[i+2].toByte(), bytes[i+1].toByte()))
                i++

            }
            "E3" -> {
                println(String.format("XTHL"))
                i ++

            }
            "E4" -> {
                println(String.format("CPO %02X %02X", bytes[i+2].toByte(), bytes[i+1].toByte()))
                i++

            }
            "E5" -> {
                println("PUSH  PSW")
                i++
            }
            "E6" -> {
                println(String.format("ANI %02X", bytes[i+1].toByte()))
                i++

            }
            "E7" -> {
                println(String.format("RST 4"))
                i ++

            }
            "E8" -> {
                println(String.format("RPE"))
                i ++

            }
            "E9" -> {
                println(String.format("PCHL"))
                i ++

            }
            "EA" -> {
                println(String.format("JPE %02X %02X", bytes[i+2].toByte(), bytes[i+1].toByte()))
                i++

            }
            "EB" -> {
                println(String.format("XCGH"))
                i ++

            }
            "EC" -> {
                println(String.format("CPE %02X %02X", bytes[i+2].toByte(), bytes[i+1].toByte()))
                i++

            }
            "ED" -> {
                println(String.format("ED   -----"))
                i++

            }
            "EE" -> {
                println(String.format("XRI %02X", bytes[i+1].toByte()))
                i++

            }
            "EF" -> {
                println(String.format("RST 5"))
                i ++

            }

            "F0" -> {
                println(String.format("RP"))
                i ++

            }
            "F1" -> {
                println(String.format("POP PSW"))
                i++
            }
            "F2" -> {
                println(String.format("JP %02X %02X", bytes[i+2].toByte(), bytes[i+1].toByte()))
                i++

            }
            "F3" -> {
                println(String.format("DI"))
                i ++

            }
            "F4" -> {
                println(String.format("CP %02X %02X", bytes[i+2].toByte(), bytes[i+1].toByte()))
                i++

            }
            "F5" -> {
                println("PUSH  PSW")
                i++
            }
            "F6" -> {
                println(String.format("ORI %02X", bytes[i+1].toByte()))
                i++

            }
            "F7" -> {
                println(String.format("RST 6"))
                i ++

            }
            "F8" -> {
                println(String.format("RM"))
                i ++

            }
            "F9" -> {
                println(String.format("SPHL"))
                i ++

            }
            "FA" -> {
                println(String.format("JM %02X %02X", bytes[i+2].toByte(), bytes[i+1].toByte()))
                i++

            }
            "FB" -> {
                println(String.format("EI"))
                i ++

            }
            "FC" -> {
                println(String.format("CM %02X %02X", bytes[i+2].toByte(), bytes[i+1].toByte()))
                i++

            }
            "FD" -> {
                println(String.format("FD This shouldnt be called"))
                i++

            }
            "FE" -> {
                println(String.format("CPI %02X", bytes[i+1].toByte()))
                i++

            }
            "FF" -> {
                println(String.format("RST 7"))
                i ++

            }


        }

    }
}