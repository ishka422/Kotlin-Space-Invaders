import java.io.File

fun main() {

    val bytes = File("invaders.h").readBytes()
    var i = 0
    var st = ""
    while (i < bytes.size) {
        st = String.format("%02X", bytes[i])

        when (st) {
            "00" -> {
                println("NOP")
                i++
            }
            "01" -> {
                println(String.format("LXI, B: %02X %02X", bytes[i + 1], bytes[i + 2]))
                i += 2
            }
            "02" -> {
                println(st)
                i = bytes.size

            }
            "03" -> {
                println(st)
                i = bytes.size

            }
            "04" -> {
                println(st)
                i = bytes.size

            }
            "05" -> {
                println(st)
                i = bytes.size

            }
            "06" -> {
                println(st)
                i = bytes.size

            }
            "07" -> {
                println(st)
                i = bytes.size

            }
            "08" -> {
                println(st)
                i = bytes.size

            }
            "09" -> {
                println(st)
                i = bytes.size

            }
            "0A" -> {
                println(st)
                i = bytes.size

            }
            "0B" -> {
                println(st)
                i = bytes.size

            }
            "0C" -> {
                println(st)
                i = bytes.size

            }
            "0D" -> {
                println(st)
                i = bytes.size

            }
            "0E" -> {
                println(st)
                i = bytes.size

            }
            "0F" -> {
                println("RP")
                i++

            }


            "10" -> {
                println(st)
                i = bytes.size

            }
            "11" -> {
                println(st)
                i++
            }
            "12" -> {
                println(st)
                i = bytes.size

            }
            "13" -> {
                println(st)
                i = bytes.size

            }
            "14" -> {
                println(st)
                i = bytes.size

            }
            "15" -> {
                println(st)
                i = bytes.size

            }
            "16" -> {
                println(st)
                i = bytes.size

            }
            "17" -> {
                println(st)
                i = bytes.size

            }
            "18" -> {
                println(st)
                i = bytes.size

            }
            "19" -> {
                println(st)
                i = bytes.size

            }
            "1A" -> {
                println(st)
                i = bytes.size

            }
            "1B" -> {
                println(st)
                i = bytes.size

            }
            "1C" -> {
                println(st)
                i = bytes.size

            }
            "1D" -> {
                println(st)
                i = bytes.size

            }
            "1E" -> {
                println(st)
                i = bytes.size

            }
            "1F" -> {
                println(st)
                i = bytes.size

            }

            "20" -> {
                println(st)
                i = bytes.size

            }
            "21" -> {
                println(String.format("LXI    H, %02X %02X", bytes[i+1], bytes[i+2]))
                i += 3
            }
            "22" -> {
                println(st)
                i = bytes.size

            }
            "23" -> {
                println(st)
                i = bytes.size

            }
            "24" -> {
                println(st)
                i = bytes.size

            }
            "25" -> {
                println(st)
                i = bytes.size

            }
            "26" -> {
                println(st)
                i = bytes.size

            }
            "27" -> {
                println(st)
                i = bytes.size

            }
            "28" -> {
                println(st)
                i = bytes.size

            }
            "29" -> {
                println(st)
                i = bytes.size

            }
            "2A" -> {
                println(st)
                i = bytes.size

            }
            "2B" -> {
                println(st)
                i = bytes.size

            }
            "2C" -> {
                println(st)
                i = bytes.size

            }
            "2D" -> {
                println(st)
                i = bytes.size

            }
            "2E" -> {
                println(st)
                i = bytes.size

            }
            "2F" -> {
                println(st)
                i = bytes.size

            }

            "30" -> {
                println(st)
                i = bytes.size

            }
            "31" -> {
                println(st)
                i++
            }
            "32" -> {
                println(String.format("STA    a16, %02X %02X", bytes[i+1], bytes [i+2]))
                i += 3

            }
            "33" -> {
                println(st)
                i = bytes.size

            }
            "34" -> {
                println(st)
                i = bytes.size

            }
            "35" -> {
                println("DCR    M")
                i++

            }
            "36" -> {
                println(st)
                i = bytes.size

            }
            "37" -> {
                println(st)
                i = bytes.size

            }
            "38" -> {
                println(st)
                i = bytes.size

            }
            "39" -> {
                println(st)
                i = bytes.size

            }
            "3A" -> {
                println(st)
                i = bytes.size

            }
            "3B" -> {
                println(st)
                i = bytes.size

            }
            "3C" -> {
                println(st)
                i = bytes.size

            }
            "3D" -> {
                println(st)
                i = bytes.size

            }
            "3E" -> {
                println(String.format("MVI    A, %02X", bytes[i+1]))
                i +=2

            }
            "3F" -> {
                println(st)
                i = bytes.size

            }

            "40" -> {
                println(st)
                i = bytes.size

            }
            "41" -> {
                println(st)
                i++
            }
            "42" -> {
                println(st)
                i = bytes.size

            }
            "43" -> {
                println(st)
                i = bytes.size

            }
            "44" -> {
                println(st)
                i = bytes.size

            }
            "45" -> {
                println(st)
                i = bytes.size

            }
            "46" -> {
                println(st)
                i = bytes.size

            }
            "47" -> {
                println(st)
                i = bytes.size

            }
            "48" -> {
                println(st)
                i = bytes.size

            }
            "49" -> {
                println(st)
                i = bytes.size

            }
            "4A" -> {
                println(st)
                i = bytes.size

            }
            "4B" -> {
                println(st)
                i = bytes.size

            }
            "4C" -> {
                println(st)
                i = bytes.size

            }
            "4D" -> {
                println(st)
                i = bytes.size

            }
            "4E" -> {
                println(st)
                i = bytes.size

            }
            "4F" -> {
                println(st)
                i = bytes.size

            }

            "50" -> {
                println(st)
                i = bytes.size

            }
            "51" -> {
                println(st)
                i++
            }
            "52" -> {
                println(st)
                i = bytes.size

            }
            "53" -> {
                println(st)
                i = bytes.size

            }
            "54" -> {
                println(st)
                i = bytes.size

            }
            "55" -> {
                println(st)
                i = bytes.size

            }
            "56" -> {
                println(st)
                i = bytes.size

            }
            "57" -> {
                println(st)
                i = bytes.size

            }
            "58" -> {
                println(st)
                i = bytes.size

            }
            "59" -> {
                println(st)
                i = bytes.size

            }
            "5A" -> {
                println(st)
                i = bytes.size

            }
            "5B" -> {
                println(st)
                i = bytes.size

            }
            "5C" -> {
                println(st)
                i = bytes.size

            }
            "5D" -> {
                println(st)
                i = bytes.size

            }
            "5E" -> {
                println(st)
                i = bytes.size

            }
            "5F" -> {
                println(st)
                i = bytes.size

            }

            "60" -> {
                println(st)
                i = bytes.size

            }
            "61" -> {
                println(st)
                i++
            }
            "62" -> {
                println(st)
                i = bytes.size

            }
            "63" -> {
                println(st)
                i = bytes.size

            }
            "64" -> {
                println(st)
                i = bytes.size

            }
            "65" -> {
                println(st)
                i = bytes.size

            }
            "66" -> {
                println(st)
                i = bytes.size

            }
            "67" -> {
                println(st)
                i = bytes.size

            }
            "68" -> {
                println(st)
                i = bytes.size

            }
            "69" -> {
                println(st)
                i = bytes.size

            }
            "6A" -> {
                println(st)
                i = bytes.size

            }
            "6B" -> {
                println(st)
                i = bytes.size

            }
            "6C" -> {
                println(st)
                i = bytes.size

            }
            "6D" -> {
                println(st)
                i = bytes.size

            }
            "6E" -> {
                println(st)
                i = bytes.size

            }
            "6F" -> {
                println(st)
                i = bytes.size

            }

            "70" -> {
                println(st)
                i = bytes.size

            }
            "71" -> {
                println(st)
                i++
            }
            "72" -> {
                println(st)
                i = bytes.size

            }
            "73" -> {
                println(st)
                i = bytes.size

            }
            "74" -> {
                println(st)
                i = bytes.size

            }
            "75" -> {
                println(st)
                i = bytes.size

            }
            "76" -> {
                println(st)
                i = bytes.size

            }
            "77" -> {
                println(st)
                i = bytes.size

            }
            "78" -> {
                println(st)
                i = bytes.size

            }
            "79" -> {
                println(st)
                i = bytes.size

            }
            "7A" -> {
                println(st)
                i = bytes.size

            }
            "7B" -> {
                println(st)
                i = bytes.size

            }
            "7C" -> {
                println(st)
                i = bytes.size

            }
            "7D" -> {
                println(st)
                i = bytes.size

            }
            "7E" -> {
                println(st)
                i = bytes.size

            }
            "7F" -> {
                println(st)
                i = bytes.size

            }

            "80" -> {
                println(st)
                i = bytes.size

            }
            "81" -> {
                println(st)
                i++
            }
            "82" -> {
                println(st)
                i = bytes.size

            }
            "83" -> {
                println(st)
                i = bytes.size

            }
            "84" -> {
                println(st)
                i = bytes.size

            }
            "85" -> {
                println(st)
                i = bytes.size

            }
            "86" -> {
                println(st)
                i = bytes.size

            }
            "87" -> {
                println(st)
                i = bytes.size

            }
            "88" -> {
                println(st)
                i = bytes.size

            }
            "89" -> {
                println(st)
                i = bytes.size

            }
            "8A" -> {
                println(st)
                i = bytes.size

            }
            "8B" -> {
                println(st)
                i = bytes.size

            }
            "8C" -> {
                println(st)
                i = bytes.size

            }
            "8D" -> {
                println(st)
                i = bytes.size

            }
            "8E" -> {
                println(st)
                i = bytes.size

            }
            "8F" -> {
                println(st)
                i = bytes.size

            }

            "90" -> {
                println(st)
                i = bytes.size

            }
            "91" -> {
                println(st)
                i++
            }
            "92" -> {
                println(st)
                i = bytes.size

            }
            "93" -> {
                println(st)
                i = bytes.size

            }
            "94" -> {
                println(st)
                i = bytes.size

            }
            "95" -> {
                println(st)
                i = bytes.size

            }
            "96" -> {
                println(st)
                i = bytes.size

            }
            "97" -> {
                println(st)
                i = bytes.size

            }
            "98" -> {
                println(st)
                i = bytes.size

            }
            "99" -> {
                println(st)
                i = bytes.size

            }
            "9A" -> {
                println(st)
                i = bytes.size

            }
            "9B" -> {
                println(st)
                i = bytes.size

            }
            "9C" -> {
                println(st)
                i = bytes.size

            }
            "9D" -> {
                println(st)
                i = bytes.size

            }
            "9E" -> {
                println(st)
                i = bytes.size

            }
            "9F" -> {
                println(st)
                i = bytes.size

            }

            "A0" -> {
                println(st)
                i = bytes.size

            }
            "A1" -> {
                println(st)
                i++
            }
            "A2" -> {
                println(st)
                i = bytes.size

            }
            "A3" -> {
                println(st)
                i = bytes.size

            }
            "A4" -> {
                println(st)
                i = bytes.size

            }
            "A5" -> {
                println(st)
                i = bytes.size

            }
            "A6" -> {
                println(st)
                i = bytes.size

            }
            "A7" -> {
                println(st)
                i = bytes.size

            }
            "A8" -> {
                println(st)
                i = bytes.size

            }
            "A9" -> {
                println(st)
                i = bytes.size

            }
            "AA" -> {
                println(st)
                i = bytes.size

            }
            "AB" -> {
                println(st)
                i = bytes.size

            }
            "AC" -> {
                println(st)
                i = bytes.size

            }
            "AD" -> {
                println(st)
                i = bytes.size

            }
            "AE" -> {
                println(st)
                i = bytes.size

            }
            "AF" -> {
                println(st)
                i = bytes.size

            }

            "B0" -> {
                println(st)
                i = bytes.size

            }
            "B1" -> {
                println(st)
                i++
            }
            "B2" -> {
                println(st)
                i = bytes.size

            }
            "B3" -> {
                println(st)
                i = bytes.size

            }
            "B4" -> {
                println(st)
                i = bytes.size

            }
            "B5" -> {
                println(st)
                i = bytes.size

            }
            "B6" -> {
                println(st)
                i = bytes.size

            }
            "B7" -> {
                println(st)
                i = bytes.size

            }
            "B8" -> {
                println(st)
                i = bytes.size

            }
            "B9" -> {
                println(st)
                i = bytes.size

            }
            "BA" -> {
                println(st)
                i = bytes.size

            }
            "BB" -> {
                println(st)
                i = bytes.size

            }
            "BC" -> {
                println(st)
                i = bytes.size

            }
            "BD" -> {
                println(st)
                i = bytes.size

            }
            "BE" -> {
                println(st)
                i = bytes.size

            }
            "BF" -> {
                println(st)
                i = bytes.size

            }

            "C0" -> {
                println(st)
                i = bytes.size

            }
            "C1" -> {
                println(st)
                i++
            }
            "C2" -> {
                println(st)
                i = bytes.size

            }
            "C3" -> {
                println(String.format("JMP  %02X %02X", bytes[i + 1], bytes[i + 2]))
                i += 3
            }
            "C4" -> {
                println(st)
                i = bytes.size

            }
            "C5" -> {
                println("PUSH B")
                i++

            }
            "C6" -> {
                println(st)
                i = bytes.size

            }
            "C7" -> {
                println(st)
                i = bytes.size

            }
            "C8" -> {
                println(st)
                i = bytes.size

            }
            "C9" -> {
                println(st)
                i = bytes.size

            }
            "CA" -> {
                println(st)
                i = bytes.size

            }
            "CB" -> {
                println(st)
                i = bytes.size

            }
            "CC" -> {
                println(st)
                i = bytes.size

            }
            "CD" -> {
                println(String.format("CALL     a16, %02X %02X", bytes[i+1], bytes[i+2]))
                i += 3

            }
            "CE" -> {
                println(st)
                i = bytes.size

            }
            "CF" -> {
                println(st)
                i = bytes.size

            }

            "D0" -> {
                println(st)
                i = bytes.size

            }
            "D1" -> {
                println(st)
                i++
            }
            "D2" -> {
                println(st)
                i = bytes.size

            }
            "D3" -> {
                println(st)
                i = bytes.size

            }
            "D4" -> {
                println(st)
                i = bytes.size

            }
            "D5" -> {
                println("PUSH D")
                i++

            }
            "D6" -> {
                println(st)
                i = bytes.size

            }
            "D7" -> {
                println(st)
                i = bytes.size

            }
            "D8" -> {
                println(st)
                i = bytes.size

            }
            "D9" -> {
                println(st)
                i = bytes.size

            }
            "DA" -> {
                println(String.format("JC     a16, %02X %02X", bytes[i+1], bytes[i+2]))
                i +=3

            }
            "DB" -> {
                println(String.format("IN     d8, %02X", bytes[i+1]))
                i += 2

            }
            "DC" -> {
                println(st)
                i = bytes.size

            }
            "DD" -> {
                println(st)
                i = bytes.size

            }
            "DE" -> {
                println(st)
                i = bytes.size

            }
            "DF" -> {
                println(st)
                i = bytes.size

            }


            "E0" -> {
                println(st)
                i = bytes.size

            }
            "E1" -> {
                println(st)
                i++
            }
            "E2" -> {
                println(st)
                i = bytes.size

            }
            "E3" -> {
                println(st)
                i = bytes.size

            }
            "E4" -> {
                println(st)
                i = bytes.size

            }
            "E5" -> {
                println("PUSH  PSW")
                i++
            }
            "E6" -> {
                println(st)
                i = bytes.size

            }
            "E7" -> {
                println(st)
                i = bytes.size

            }
            "E8" -> {
                println(st)
                i = bytes.size

            }
            "E9" -> {
                println(st)
                i = bytes.size

            }
            "EA" -> {
                println(st)
                i = bytes.size

            }
            "EB" -> {
                println(st)
                i = bytes.size

            }
            "EC" -> {
                println(st)
                i = bytes.size

            }
            "ED" -> {
                println(st)
                i = bytes.size

            }
            "EE" -> {
                println(st)
                i = bytes.size

            }
            "EF" -> {
                println(st)
                i = bytes.size

            }

            "F0" -> {
                println(st)
                i = bytes.size

            }
            "F1" -> {
                println(st)
                i++
            }
            "F2" -> {
                println(st)
                i = bytes.size

            }
            "F3" -> {
                println(st)
                i = bytes.size

            }
            "F4" -> {
                println(st)
                i = bytes.size

            }
            "F5" -> {
                println("PUSH  PSW")
                i++
            }
            "F6" -> {
                println(st)
                i = bytes.size

            }
            "F7" -> {
                println(st)
                i = bytes.size

            }
            "F8" -> {
                println(st)
                i = bytes.size

            }
            "F9" -> {
                println(st)
                i = bytes.size

            }
            "FA" -> {
                println(st)
                i = bytes.size

            }
            "FB" -> {
                println(st)
                i = bytes.size

            }
            "FC" -> {
                println(st)
                i = bytes.size

            }
            "FD" -> {
                println(st)
                i = bytes.size

            }
            "FE" -> {
                println(st)
                i = bytes.size

            }
            "FF" -> {
                println(st)
                i = bytes.size

            }


        }
    }
}

