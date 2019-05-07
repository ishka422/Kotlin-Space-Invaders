import java.awt.BorderLayout
import java.awt.Dimension
import java.io.File
import javax.swing.ImageIcon
import javax.swing.JFrame
import javax.swing.JLabel
import kotlin.system.exitProcess


@kotlin.ExperimentalUnsignedTypes
var last1000index =0
var last1000:UIntArray = UIntArray(1000)
var last1000sp:UIntArray = UIntArray(1000)
var lastString = ""
//array of how many cycles each operaction should take
val cycles8080 = intArrayOf(
	4, 10, 7, 5, 5, 5, 7, 4, 4, 10, 7, 5, 5, 5, 7, 4,
	4, 10, 7, 5, 5, 5, 7, 4, 4, 10, 7, 5, 5, 5, 7, 4,
	4, 10, 16, 5, 5, 5, 7, 4, 4, 10, 16, 5, 5, 5, 7, 4,
	4, 10, 13, 5, 10, 10, 10, 4, 4, 10, 13, 5, 5, 5, 7, 4,

	5, 5, 5, 5, 5, 5, 7, 5, 5, 5, 5, 5, 5, 5, 7, 5,
	5, 5, 5, 5, 5, 5, 7, 5, 5, 5, 5, 5, 5, 5, 7, 5,
	5, 5, 5, 5, 5, 5, 7, 5, 5, 5, 5, 5, 5, 5, 7, 5,
	7, 7, 7, 7, 7, 7, 7, 7, 5, 5, 5, 5, 5, 5, 7, 5,

	4, 4, 4, 4, 4, 4, 7, 4, 4, 4, 4, 4, 4, 4, 7, 4,
	4, 4, 4, 4, 4, 4, 7, 4, 4, 4, 4, 4, 4, 4, 7, 4,
	4, 4, 4, 4, 4, 4, 7, 4, 4, 4, 4, 4, 4, 4, 7, 4,
	4, 4, 4, 4, 4, 4, 7, 4, 4, 4, 4, 4, 4, 4, 7, 4,

	11, 10, 10, 10, 17, 11, 7, 11, 11, 10, 10, 10, 10, 17, 7, 11,
	11, 10, 10, 10, 17, 11, 7, 11, 11, 10, 10, 10, 10, 17, 7, 11,
	11, 10, 10, 18, 17, 11, 7, 11, 11, 5, 10, 5, 17, 17, 7, 11,
	11, 10, 10, 4, 17, 11, 7, 11, 11, 5, 10, 4, 17, 17, 7, 11
        )

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
    var emulate = Registers()
    for(i in 0 until bytes.size){
        emulate.memory[i] = bytes[i]
    }
    var lastTimer = 0L
    var nextInterupt = 0L
    var whichInterupt = 1
    var nextFrame = 0L

    while(emulate.pc.toInt() < bytes.size){



//        var now = System.currentTimeMillis()
//        if (lastTimer == 0L){
//            lastTimer = now
//            nextInterupt = lastTimer + 56 //16//48
//            whichInterupt = 1
//
//        }
//
//        if((emulate.int_enable ==1) && now > nextInterupt){
//            if(whichInterupt == 1){
//                emulate.GenerateInterupt(1)
//                whichInterupt = 2
//            }
//            else{
//                emulate.GenerateInterupt(2)
//                whichInterupt = 1
//            }
//            nextInterupt = now + 28 //8//24
//        }
//        if((now - nextFrame) > 16){
//            nextFrame = now+16
//            display.icon = ImageIcon(arrayToBitmap(emulate.memory))
//            display.repaint()
//            screen.add(display)
//            display.repaint()
//            screen.revalidate()
//            screen.repaint()
//        }
//        var sinceLast = now - lastTimer
//        var cyclesToCatchUp = (2 * sinceLast).toLong()*1000
        var now = System.nanoTime()/1000




        //var now = System.currentTimeMillis()
        if (lastTimer == 0L){
            lastTimer = now
            nextInterupt = lastTimer + 16000
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
            nextInterupt = now + 8000
        }
        if((now - nextFrame) > 16000){
            nextFrame = now+16000
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
        var cyclesToCatchUp = (2 * sinceLast).toLong()
        var cycles = 0
        while(cyclesToCatchUp > cycles) {
            var opcode = String.format("%02x",emulate.memory[emulate.pc.toInt()].toByte()).toUpperCase()
            if (opcode == "DB") {


                var port = emulate.memory[emulate.pc.toInt() + 1]
                emulate.SpaceInvadersIn(port)
                emulate.pc++
                emulate.pc++
                cycles +=3
            } else if (opcode == "D3") {


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
//        opcount++
//        if(opcount%25 == 0){
//            Thread.sleep(1)
//        }

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



