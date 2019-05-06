import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.image.BufferedImage
import javax.swing.ImageIcon
import javax.swing.JFrame
import javax.swing.JLabel

fun makeDisplay(){
    val screen = JFrame("Space Invaders")
    var display = JLabel(ImageIcon())
    screen.getContentPane().add(display, BorderLayout.CENTER)
    screen.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)
    screen.setSize(Dimension(224,256))
    screen.setLocationRelativeTo(null)
    screen.setVisible(true)

}

@kotlin.ExperimentalUnsignedTypes

fun arrayToBitmap(stateMemory:UByteArray): BufferedImage {
    val img = BufferedImage(224,256, BufferedImage.TYPE_BYTE_BINARY)
    var i = 0
    var j = 0
    val offset = 0x2400

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

