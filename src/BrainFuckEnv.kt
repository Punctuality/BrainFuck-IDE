import com.sun.javaws.exceptions.InvalidArgumentException
import java.util.*

class BrainFuckEnv(private val resolution: Int){

    private val scan = Scanner(System.`in`)
    private var environment = ushortArrayOf(0.toUShort())
    private var curPos: Int = 30000-1

    private var errorMSG = false
    private var msgOUT = ""
    private val commandsList = charArrayOf('+', '-', '>', '<', '.', ',', '[', ']')

    init{
        resetEnv()
    }

    fun resetEnv(){
        this.environment = UShortArray(resolution, { 0.toUShort() })
        curPos = 0
        msgOUT = ""
    }

    fun showOutput() : String{
        return msgOUT
    }

    fun addCommand(listOfCommands: String, showStateOnEnd: Boolean = false){
        var iter: Int = 0
        while (iter <= listOfCommands.lastIndex){
            val command = listOfCommands[iter]
            if (command in this.commandsList){
                //parser
                when(command){
                    '+' -> incr()
                    '-' -> decr()
                    '<' -> leftS()
                    '>' -> rightS()
                    '.' -> {
//                        print(show())
                        msgOUT += show()
                    }
                    ',' -> read()
                    '[' -> {
                        iter = cycle(iter, listOfCommands)
                    }
                }
            } else {
                if (this.errorMSG) println("Typo: $iter\n\"$command\" isn`t a valid command for BrainFuck language.")
            }
            iter++
        }

        if (showStateOnEnd) println(this.showEnv())
    }

    // [
    // ]
    private fun cycle(start: Int, listOfCommands: String) : Int{
        var curCommand = listOfCommands.drop(start+1)
        var end = 0
        var counter: Int = 1
        for(iter: Int in 0 until curCommand.length){
            val chr = curCommand[iter]
            if (chr == '['){
                counter++
            } else if (chr == ']') {
                counter--
                if (counter == 0) {
                    end = start+iter+1
                    break
                }
            }
        }
        curCommand = curCommand.dropLast(listOfCommands.lastIndex-end+1)
        while (true) {
            if (this.environment[this.curPos] == 0.toUShort()) break
            this.addCommand(curCommand, false)
        }
        return end
    }

    //+
    private fun incr(pos: Int = curPos){
        this.environment[pos]++
    }
    //-
    private fun decr(pos: Int = curPos){
        this.environment[pos]--
    }
    // <
    private fun leftS(){
        this.curPos--
        if (this.curPos < 0) {
            this.curPos = this.environment.size-1
        }
    }
    // >
    private fun rightS(){
        this.curPos++
        if (this.curPos > this.environment.size-1){
            this.curPos = 0
        }
    }
    // .
    private fun show(): Char{
        return (this.environment[this.curPos].toInt().toChar())
    }
    // ,
    private fun read(){
        this.environment[this.curPos]= scan.nextLine().first().toInt().toUShort()
    }

    fun showEnv() : String {
//        println()
        val addit = this.environment.drop(curPos+1).joinToString("_", postfix = "| <- Stop" )
        val envState: String = "\n"+this.environment.joinToString("_", "Start -> |", limit = curPos, truncated = "#${this.environment[curPos]}#"+addit)
        return envState
    }
}