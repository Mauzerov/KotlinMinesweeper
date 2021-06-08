import java.awt.Point
import kotlin.math.abs
import kotlin.random.Random

operator fun Point.plus(other : Point) : Point {
    return Point(this.x + other.x, this.y + other.y)
}

fun Point.toString() : String {
    return "(${this.x}, ${this.y})"
}

fun <T> ArrayList<ArrayList<T>>.atPoint(point: Point) : T {
    return this[point.x][point.y]
}

fun <T> fillRandomly2dArray(
        default: ArrayList<ArrayList<T>>,
        elements: Array<T>,
        arraySize: Int,
        amount: Double):
        ArrayList< ArrayList<T> > {
    return fillRandomly2dArray(
            default, elements, arraySize, amount.toInt()
    )
}

fun <T> fillRandomly2dArray(
        default: ArrayList<ArrayList<T>>,
        elements: Array<T>,
        arraySize: Int,
        amount: Float):
        ArrayList< ArrayList<T> > {
    return fillRandomly2dArray(
            default, elements, arraySize, amount.toInt()
    )
}

fun <T> fillRandomly2dArray(
        default: ArrayList<ArrayList<T>>,
        elements: Array<T>,
        arraySize: Int,
        amount: Int):
        ArrayList< ArrayList<T> > {

    // Function Globals
    var arrayX = 0
    var arrayY = 0
    var _amount = amount

    fun nextRightIndex() {
        if (++arrayX >= arraySize) {
            arrayX = 0
            if (++arrayY >= arraySize)
                arrayY = 0
        }
    }

    // Safety Check
    if (amount > arraySize * arraySize)
        throw ArrayIndexOutOfBoundsException("all of bombs cannot be placed inside")

    if (elements.size != 2)
        throw ArrayIndexOutOfBoundsException("incorrect array size")

    // Filling All With First Value / Resize It
    for (i in 0 until arraySize) {
        default.add(ArrayList(arraySize))
        default[i].fill(arraySize, elements[0])
    }

    while (_amount-- != 0) {
        arrayX = abs(Random.nextInt()) % arraySize
        arrayY = abs(Random.nextInt()) % arraySize

        while (default[arrayX][arrayY] == elements[1]) {
            nextRightIndex()
        }

        default[arrayX][arrayY] = elements[1]
    }

    return default
}

fun <T, K> findRandomBomb(array: ArrayList<ArrayList<T>>, selected: ArrayList<ArrayList<K>>, bombIndicator: T, selectedIndicator: K) : Point {
    while ( true ) {
        val x = abs(Random.nextInt()) % array.size
        val y = abs(Random.nextInt()) % array.size
        if (array[x][y] == bombIndicator && selected[x][y] != selectedIndicator)
            return Point(x, y)
    }
}