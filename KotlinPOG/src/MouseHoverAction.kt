import java.awt.Color
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.awt.event.MouseListener

fun Boolean.toInt() = if (this) 1 else 0
fun <T> ArrayList<T>.fill(size: Int, with: T) {
    for (i in 0 until size) this.add(with)
}

fun randomRange(from: Int, to: Int): List<Int> {
    return (from..to).shuffled()
}

private const val begin: String = "<span style='color: rgb(%d,%d,%d); font-size: %d;'>"
private const val end: String = "</span></html>"
fun addLighterFontTextHtml(oldText: String, color: Color, text: String, size: Int = 12) : String{
    return "<html>" + oldText + String.format(begin, color.red, color.green, color.blue, size) + text + end
}

fun createMouseAdapter(enter : () -> Unit, exit : () -> Unit) : MouseListener{
    return object : MouseAdapter() {
        override fun mouseEntered(e: MouseEvent) = enter()
        override fun mouseExited(e: MouseEvent) = exit()
    }
}

fun createClickedMouseAdapter(left: () -> Unit = {}, middle: () -> Unit = {}, right: () -> Unit = {}) : MouseListener{
    return object : MouseAdapter() {
        override fun mousePressed(e: MouseEvent?) {
            when (e?.button) {
                MouseEvent.BUTTON1 -> left()
                MouseEvent.BUTTON2 -> middle()
                MouseEvent.BUTTON3 -> right()
            }
        }
    }
}
