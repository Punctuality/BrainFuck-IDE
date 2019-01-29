import java.awt.BorderLayout
import java.awt.FlowLayout
import java.awt.GridLayout
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.io.File
import java.io.PrintWriter
import java.lang.NumberFormatException
import java.util.*
import javax.swing.*
import javax.swing.filechooser.FileNameExtensionFilter

fun main(args: Array<String>){
    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())
    val mainFrame = BrainFIDE(true, 100)
    println(mainFrame)
}


class BrainFIDE(visible: Boolean, private var resolution: Int) : JFrame("BrainFuck IDE"){
    private val textField = JTextArea("# Type your code here!", 10, 2)
    private val scroll = JScrollPane(textField, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
        JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED)
    private val panel = JPanel()
    private val compilerButton = JButton("Run!")
    private val menuBar = JMenuBar()
    private var file = File("")
    private val fileChooser = javax.swing.JFileChooser()

    class EnvStateFrame : JFrame("Output"){
        private val output = JTextArea()
        private val state = JTextArea()
        private val scrollOutput = JScrollPane(output, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
            JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED)
        private val scrollState = JScrollPane(state, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
            JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED)
        init {
            output.lineWrap = true
            state.lineWrap = true

            output.isEditable = false
            state.isEditable = false

            this.contentPane.layout = GridLayout(2,1)
            this.add(scrollOutput)
            this.add( scrollState)

            this.setSize(640, 480)
            this.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        }

        fun updateInfo(output: String, state: String){
            this.output.text = output
            this.state.text = state
        }
    }

    private var environment = BrainFuckEnv(resolution)
    private val answerFrame = EnvStateFrame()

    init{
        //Configuring JMenuBar()
        val fileMenu = JMenu("File")
        val viewMenu = JMenu("View")
        val preferencesMenu = JMenu("Preferences")

        val newMenu = JMenuItem("New")
        val openMenu = JMenuItem("Open")
        val saveMenu = JMenuItem("Save")
        val saveAsMenu = JMenuItem("Save as")
        //Separator
        val exitMenu = JMenuItem("Exit")

        val showOutput = JMenuItem("Show Output/State")

        val setResolutionMenu = JMenuItem("Set Env. Resolution")
        val helpMenu = JMenuItem("Help!!!")

        newMenu.addActionListener {
            file = File("")
            textField.text = ""
            this.name = "BrainFuck IDE"
        }
        openMenu.addActionListener {
            val response: Int = fileChooser.showDialog(null, "Choose file to open")
            if (response == JFileChooser.APPROVE_OPTION){
                file = fileChooser.selectedFile
                textField.text = this.fileReader(file)
                this.name = "BrainFuck IDE: ${file.name}"
            }
        }
        saveMenu.addActionListener {
            fileSaver(file, textField.text)
        }
        saveAsMenu.addActionListener {
            val response: Int = fileChooser.showDialog(null, "Choose file to save")
            if (response == JFileChooser.APPROVE_OPTION){
                file = fileChooser.selectedFile
                fileSaver(file, textField.text)
                this.name = "BrainFuck IDE: ${file.name}"
            }
        }
        exitMenu.addActionListener {
            saveMenu.doClick()
            System.exit(0)
        }

        showOutput.addActionListener {
            answerFrame.isVisible = !answerFrame.isVisible
        }

        setResolutionMenu.addActionListener {
            val setResFrame = JFrame()
            val tempField = JTextField("$resolution", 20)
            val tempButton = JButton("Change state")
            tempButton.addActionListener{
                try{
                    resolution = tempField.text.toInt()
                    environment = BrainFuckEnv(resolution)
                } catch(exp: NumberFormatException){
                    println("WrongNumber")
                }
                setResFrame.isVisible = false
            }
            setResFrame.layout = FlowLayout()
            setResFrame.apply{
                add(tempField)
                add(tempButton)
            }
            setResFrame.pack()
            setResFrame.isVisible = true
        }

        val bfFilter = FileNameExtensionFilter("BrainFuck source files", "bf")
        fileChooser.fileFilter = bfFilter

        fileMenu.apply {
            add(newMenu)
            add(openMenu)
            add(saveMenu)
            add(saveAsMenu)
            addSeparator()
            add(exitMenu)
        }
        viewMenu.apply {
            add(showOutput)
        }
        preferencesMenu.apply{
            add(setResolutionMenu)
            add(helpMenu)
        }

        menuBar.apply {
            add(fileMenu)
            add(viewMenu)
            add(preferencesMenu)
        }

        // Configuring MainPanel
        this.panel.layout = BorderLayout()
        this.compilerButton.addActionListener {
            environment.resetEnv()
            val code = textField.text
            environment.addCommand(code, false)
            val output = environment.showOutput()
            val state = environment.showEnv()

            answerFrame.updateInfo(output, state)
            answerFrame.isVisible = true
        }

        this.textField.lineWrap = true
        // Adding components to panel and to frame
        this.panel.apply {
            add(BorderLayout.CENTER, scroll)
            add(BorderLayout.SOUTH, compilerButton)
        }
        this.add(this.panel)
        this.jMenuBar = menuBar

        //Configuring the frame
        this.setSize(800, 600)
        this.defaultCloseOperation = JFrame.EXIT_ON_CLOSE

        //Displaying frames
        this.isVisible = visible
    }

    private fun fileReader(file: File) : String{
        val tempScan = Scanner(file)
        var out = StringBuilder()
        while(tempScan.hasNext()){
            out.append(tempScan.nextLine())
        }
        tempScan.close()
        return out.toString()
    }

    private fun fileSaver(file: File, text: String){
        val writer = PrintWriter(file)
        writer.print(text)
        writer.flush()
        writer.close()
    }

}

