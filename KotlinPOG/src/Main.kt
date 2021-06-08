import java.awt.*
import java.lang.Integer.max
import javax.swing.*
import javax.swing.border.LineBorder
import kotlin.math.abs
import kotlin.random.Random
import kotlin.system.exitProcess

private const val size = 100
private const val appSize = 900

private val fontSize : Int = max((appSize / size).shr(1) - 1, 0)
var bombAmount = 0L
const val bombPercentage = .08

fun getSelfBombAmount() : Long {
    return bombAmount + 0
}

private var tileUnlocked = 0L
private lateinit var frame : JFrame
private lateinit var menu : JMenuBar
private lateinit var safeSelectButton : JCheckBoxMenuItem
private lateinit var restart: JMenuItem
private lateinit var hint: JMenuItem
private var safeChecked : Boolean = false
private var hintClickAmount = 0L

private lateinit var menuBasicColor : Color

private var buttons : ArrayList<ArrayList<JButton>> = ArrayList(size)
private var safeCheckedTiles : ArrayList<ArrayList<Int>> = ArrayList(size)
private var bombs : ArrayList<ArrayList<Int>> = ArrayList(size)

private fun countTotalBombs() {
    bombAmount = 0
    bombs.forEach {it.forEach { inner -> bombAmount += inner} }
}

private fun getNearbyBombAmount(x: Int, y: Int) : Int {
    var amount = 0;
    for (i in randomRange(-1, 1))
    for (j in randomRange(-1, 1)) {
        if (x + i !in 0 until size ||
            y + j !in 0 until size) continue

        if (!buttons[x + i][y + j].isEnabled &&
            safeCheckedTiles[x + i][y + j] != 2) continue

        amount += bombs[x + i][y + j]
    }
    return amount
}

private fun disableButton(x: Int, y: Int, amount: Int ) = disableButton(Point(x, y), amount)
private fun disableButton(point: Point, amount: Int) {
    // Change Displayed text
    buttons.atPoint(point).text = if (amount == 0) "" else
        String.format("<html><span style='font-size: %dpx; margin: 0; padding: 0;'>%d</span></html>", fontSize, amount)
    // Disable Clickable
    buttons.atPoint(point).isEnabled = false

    // Change Color
    val lightness = abs(Random.nextInt(1, 20)) % 20
    buttons.atPoint(point).background = Color(lightness, lightness, lightness)
    buttons.atPoint(point).border = LineBorder(Color(20, 20, 20))

    // Winning Condition
    if (size * size - ++tileUnlocked == bombAmount) {
        val message = JLabel("Victory !")
        JOptionPane.showMessageDialog(frame, message, "", TrayIcon.MessageType.INFO.ordinal)
    }
}

// TODO improve performance a bit more ( async is needed probably )

private fun doForButton(x: Int, y: Int) {
    // Declare queue
    val queue: ArrayList<Point> = arrayListOf()
    queue.add(Point(x,y))
    // Disable Clicked Button
    disableButton(queue[0], getNearbyBombAmount(x, y))

    // Iteration Variables
    var lastSize = 0
    var iterationBegin = 0

    // loop Until no buttons were added to queue
    while (lastSize != queue.size) {
        if (iterationBegin != 0)
            queue.subList(0, iterationBegin).clear()

        lastSize = queue.size
        // iterate over every new button
        for (k in 0 until lastSize) {
            val now = queue[k]

            // Collect Bomb Amount nearby
            val amount = getNearbyBombAmount(now.x, now.y)
            // If amount == 0 iterate over nearby buttons
            if (amount != 0) continue

            // Iterate Over Nearby If No Bombs Around
            for (i in randomRange(-1, 1))
                for (j in randomRange(-1, 1)) {
                    val vector = Point(i,j)

                    // Check If In Array Bounds
                    if (now.x + i !in 0 until size ||
                        now.y + j !in 0 until size) continue

                    val newPoint = now + vector
                    // Check Button Settings
                    if (!buttons.atPoint(newPoint).isEnabled &&
                            safeCheckedTiles.atPoint(newPoint) != 2
                    ) continue

                    // Check No repeats
                    if (!buttons.atPoint(newPoint).isEnabled)
                        continue

                    // Add New Point
                    queue.add(newPoint)

                    // Disable It
                    disableButton(newPoint, getNearbyBombAmount(newPoint.x, newPoint.y))
                }
        }

        // Iteration Settings
        iterationBegin = lastSize
    }
/*
    try {
        // Bombs Nearby
        var amount = 0; doForNearby(x, y) { a, b ->
            try {
                amount += if (bombs[a][b] != 0) 1 else 0
            } catch (ignored: Exception) {
            }
        }

        buttons[x][y].text = if (amount == 0) "" else
            String.format("<html><span style='font-size: %dpx; margin: 0; padding: 0;'>%d</span></html>", fontSize, amount)
        buttons[x][y].isEnabled = false
        val lightness = abs(Random.nextInt(1, 20)) % 20

        buttons[x][y].background = Color(lightness, lightness, lightness)
        buttons[x][y].border = LineBorder(Color(20, 20, 20))

        if (size * size - ++tileUnlocked == bombAmount) {
            val message = JLabel("Victory !")
            JOptionPane.showMessageDialog(frame, message, "", TrayIcon.MessageType.INFO.ordinal)
        }
        if (amount == 0) {
            doForNearby(x, y, ::doForButton)
        }
    }
    catch (ignored: Exception) { return }
    catch (ignored: java.lang.StackOverflowError){ return }
    catch (ignored: java.lang.instrument.IllegalClassFormatException) { return }
    catch (ignored: java.lang.instrument.UnmodifiableClassException) { return }
    */

}

public fun doRMBClick() {
    safeChecked = safeChecked.xor(true)
    safeSelectButton.isSelected = safeChecked
}

private fun selectButton(position: Point, background: Color, border: Color, selectionType: Int = 1) {
    selectButton(position.x, position.y, background, border, selectionType)
}

private fun selectButton(x: Int, y: Int, background: Color, border: Color, selectionType: Int = 1) {
    safeCheckedTiles[x][y] = selectionType
    buttons[x][y].background = background
    buttons[x][y].border = LineBorder(border)
}

private fun deselectButton(position: Point, background: Color, border: Color) =
    deselectButton(position.x, position.y, background, border)
private fun deselectButton(x: Int, y: Int, background: Color, border: Color) {
    safeCheckedTiles[x][y] = 0
    buttons[x][y].background = background
    buttons[x][y].border = LineBorder(border)
}

private fun setup() {
    // Setup Randomly Placed Bombs
    bombs = fillRandomly2dArray(bombs, arrayOf(0, 1), size, size * size * bombPercentage)
    countTotalBombs()

    // Setup Frame Layout
    frame.contentPane.layout = GridLayout(size, size)
    frame.isResizable = false

    // Create MenuBar
    menu = JMenuBar()
    menuBasicColor = menu.background

    // Toggleable Select Bomb Tile
    safeSelectButton = JCheckBoxMenuItem("Safe Select Bomb")
    safeSelectButton.addActionListener {
        doRMBClick()
    }
    safeSelectButton.text = addLighterFontTextHtml(
            safeSelectButton.text,
            safeSelectButton.foreground.brighter().brighter().brighter(),
            "\t\tRMB", safeSelectButton.font.size - 3)

    // Restart Game Button
    restart = JMenuItem("Restart")
    restart.addActionListener {
        hintClickAmount = 0L
        tileUnlocked = 0L
        frame.contentPane.removeAll()
        bombs.clear()
        buttons.clear()
        safeCheckedTiles.clear()
        setup()
    }

    // Hint Button -> ( Select Random Bomb )
    hint = JMenuItem("Show One Random Bomb")
    hint.addActionListener {
        if (hintClickAmount++ < getSelfBombAmount()) {
            val darkness = abs(Random.nextInt()) % 30

            val redColor = Color(57 - (darkness / 2), 179 - darkness, 57 - (darkness / 2))
            val redBorderColor = Color(57, 179, 57)

            val position = findRandomBomb(bombs, safeCheckedTiles, 1, 2)

            selectButton(position, redColor, redBorderColor, 2)
            buttons.atPoint(position).isEnabled = false
        } else
            hint.isEnabled = false
    }

    // Add Buttons To Menu
    menu.add(restart)
    menu.add(safeSelectButton)
    menu.add(hint)

    // Add Menu To Frame
    frame.jMenuBar = menu
    frame.jMenuBar.components.forEach {
        (it as JMenuItem).border = LineBorder(it.background)
        it.cursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
        it.addMouseListener(createMouseAdapter(
                { it.background = menuBasicColor.darker() }, { it.background = menuBasicColor }
        ))
    }

    countTotalBombs()
    for (i in 0 until size) {
        buttons.add(ArrayList(size))

        safeCheckedTiles.add(ArrayList(size))
        safeCheckedTiles[i].fill(size, 0)

        for (j in 0 until size) {
            buttons[i].add(JButton(""))

            val lightness = abs(Random.nextInt()) % 20
            val lightness2 = abs(Random.nextInt()) % 20

            // Background Color
            buttons[i][j].background = Color(200 - lightness, 200 - lightness, 200 - lightness)
            buttons[i][j].border = LineBorder(Color(200 - lightness2, 200 - lightness2, 200 - lightness2))

            // Font Color
            buttons[i][j].foreground = Color(255, 255, 255)

            buttons[i][j].addActionListener {

                var darkness = abs(Random.nextInt()) % 30

                val redColor = Color(179 - darkness, 57 - (darkness / 2), 57 - (darkness / 2))
                val redBorderColor = Color(179, 57, 57)
                val borderColor = Color(200 - darkness, 200 - darkness, 200 - darkness)

                darkness = abs(Random.nextInt()) % 20
                val whiteColor = Color(200 - darkness, 200 - darkness, 200 - darkness)


                when (safeChecked.toInt() or safeCheckedTiles[i][j].shl(1)) {
                    0b01 -> {
                        selectButton(i, j, redColor, redBorderColor)
                    }
                    0b11 -> {
                        deselectButton(i, j, whiteColor, borderColor)
                    }
                    0b00 -> {
                        try {
                            if (bombs[i][j] == 1) exitProcess(1)
                        } catch (ignored: Exception) {
                        }
                        Thread {
                            doForButton(i, j)
                        }.start()
                    }
                }
            }

            // Create Grid Constraints
            val grid = GridBagConstraints()
            grid.gridx = i
            grid.gridy = j

            // Apply Constraints Add Add Component
            frame.contentPane.add(buttons[i][j], grid)
        }
    }

    // Add Mouse Events To Every Component
    frame.contentPane.components.forEach {
        it.addMouseListener(createClickedMouseAdapter(right= { doRMBClick() } ))
    }
}

private fun main() {
    bombs.clear()
    // Create Frame
    frame = JFrame("Minesweeper")
    frame.contentPane.preferredSize = Dimension(appSize, appSize)
    frame.contentPane.maximumSize = Dimension(appSize, appSize)
    frame.contentPane.minimumSize = Dimension(appSize, appSize)


    frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE

    // Setup Elements
    setup()

    frame.pack()
    frame.isVisible = true

    // Set Display Position
    val dimension = Toolkit.getDefaultToolkit().screenSize
    val x = ((dimension.getWidth() - frame.width) / 2).toInt()
    val y = ((dimension.getHeight() - frame.height) / 2).toInt()
    frame.setLocation(x, y)
}