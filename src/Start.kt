import javax.swing.UIManager

fun main(args: Array<String>){
    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())
    val mainFrame = BrainFIDE(true, 100)
    println(mainFrame)
}