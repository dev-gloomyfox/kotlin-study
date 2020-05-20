import java.beans.PropertyChangeListener
import java.beans.PropertyChangeSupport
import java.time.LocalDate
import kotlin.properties.Delegates
import kotlin.reflect.KProperty

/**
 * 연산자 오버로딩과 기타 관례
 * - 연산자 오버로딩
 * - 관례: 여러 연산을 지원하기 위해 특별한 이름이 붙은 메소드
 * - 위임 프로퍼티
 *
 * 어떤 기능이 정해진 사용자 작성 함수와 연결되는 경우
 * 코틀린에서는 클래스와 연관되기 보다는 특정 함수 이름과 연관
 * 어떤 언어 기능과 미리 정해진 이름의 함수를 연결해주는 기법을 관례(convention)
 * 언어 기능을 관례에 의존하는 이유는 기존 자바 클래스를 코틀린 언어에 적용하기 위함
 */

/**
 * 1. 산술 연산자 오버로딩
 * - 관례를 사용하는 가장 단순한 예
 *
 * 1.1. 이항 산술 연산 오버로딩
 * - + 연산자 구현
 * - plus 함수 앞에 operator 키워드를 붙임, 관례를 따르는 것을 명시
 * - 확장 함수로 구현도 가능, 여기서는 대부분 확장 함수로 관례를 구현
 *
 * a * b - times
 * a / b - div
 * a % b - mod(1.1부터 rem)
 * a + b - plus
 * a - b - minus
 *
 * - 연산자 우선 순위는 표준 숫자 타입과 동일
 * - 연산자 정의 시 두 파라미터가 같은 타입일 필요는 없음
 * - 코틀린 연산자가 자동으로 교환 법칙을 지원하지는 않음, 파라미터 타입이 다르면 양쪽에 모두 정의가 필요
 * - 반환 타입도 동일할 필요 없음
 *
 */

data class Point(val x: Int, val y: Int)

operator fun Point.plus(other: Point): Point {
    return Point(x + other.x, y + other.y)
}

operator fun Point.times(scale: Double): Point {
    return Point((x * scale).toInt(), (y * scale).toInt())
}

// operator fun Double.times(p: Point): Point

operator fun Char.times(count: Int): String {
    return toString().repeat(count)
}

/**
 * 1.2. 복합 대입 연산자 오버로딩
 * - +=, -= 등의 연산자
 * - 참조를 다른 참조로 바꾸기보다 원래 객체의 내부 상태를 변경하고 싶을 때(변경 가능 컬렉션에 원소 추가), plusAssign 등의 함수 정ㅇ의 사용
 * - +=를 plus와 plusAssign 양쪽으로 컴파일 가능하기에 둘 다 정의 후 컴파일하면 컴파일 오류 발생
 * - 일반적으론 일관성있게 plus와 plusAssign을 동시에 정의하지 않고 상황에 맞게 하나만 정
 *
 * 1.3. 단항 연산자 오버로딩
 * - +a, -b와 같은 단항 연산자도 동일
 *
 * +a - unaryPlus
 * -a - unaryMinus
 * !a - not
 * ++a, a++ - inc
 * --a, a-- - dec
 */

/**
 * 2. 비교 연산자 오버로딩
 * - 모든 객체엥 비교 연산 수행 가능
 *
 * 2.1. 동등성 연산자: equals
 * - == 연산자 호출을 equals 메소드 호출로 컴파일
 * - != 연산자도 equals 호출, 뒤집어서 반환
 * - ===은 식별자 비교 연산, 동일한 객체인지 비교
 * - ===을 이용하여 자기 자신과의 비교 최적화
 * - ===은 오버로딩 불가능
 * - Any에서 상속받은 equals가 확장 함수보다 우선 순위가 높아 equals를 확장 함수로 정의할 수 없음
 */

operator fun Point.unaryMinus(): Point {
    return Point(-x, -y)
}

/**
* 2.2. 순서 연산자 : compareTo
* - 비교 연산자(<, >, <=, >=)는 compareTo 메소드 호출로 컴파일
*/

class Person(val firstName: String, val lastName: String) : Comparable<Person> {
    override fun compareTo(other: Person): Int {
        return compareValuesBy(this, other, Person::lastName, Person::firstName)
    }
}

/**
 * 3. 컬렉션과 범위에 대해 쓸 수 있는 관례
 * 3.1. 인덱스로 원소에 접근: get과 set
 * - 코틀린에서는 인덱스 연산자(a[i])도 관례를 따름
 * - 읽는 연산은 get 연산자로 변환, 쓰는 연산은 set 연산자로 변환
 * - 파라미터 타입은 임의의 타입이 될 수 있고, 여러 파라미터 사용 가능
 */

operator fun Point.get(index: Int): Int {
    return when(index) {
        0 -> x
        1 -> y
        else -> throw IndexOutOfBoundsException("Invalid coordinate $index")
    }
}

data class MutablePoint(var x: Int, var y: Int)
operator fun MutablePoint.set(index: Int, value: Int) {
    when(index) {
        0 -> x = value
        1 -> y = value
        else -> throw IndexOutOfBoundsException("Invalid coordinate $index")
    }
}

// operator fun get(rowIndex, Int, colIndex: Int)

/**
 * 3.2. in 관례
 * - 객체가 컬렉션에 들어있는지 검사, contains
 */

data class Rectangle(val upperLeft: Point, val lowerRight: Point)
operator fun Rectangle.contains(p: Point): Boolean {
    return p.x in upperLeft.x until lowerRight.x &&
            p.y in upperLeft.y until lowerRight.y
}

/**
 * 3.3 rangeTo 관례
 * - 범위를 만드려면 .. 구문 사용, rangeTo
 * - 어떤 클래스가 Comparable 인터페이스를 구현하면 rangeTo를 정의할 필요가 없음
 * - rangeTo 함수는 Comparable에 대한 확장 함수
 * - 우선 순위가 낮음, 혼동ㅇ을 피하기 위해 괄호로 인자를 감싸주면 더 좋음
 */

val now = LocalDate.now()
val vacation = now..now.plusDays(10)
println(now.plusWeeks(1) in vacation)

/**
 * 3.4. for 루프를 위한 iterator 관례
 * - for 루프는 범위 검사와 같이 in 연산자 사용
 * - 이 때 in의 의미는 list.iterator()를 호출해서 이터레이터를 얻은 다음, 그 이터레이터에 대해 hasNext와 next 호출을 반복
 */

operator fun ClosedRange<LocalDate>.iterator(): Iterator<LocalDate> =
    object : Iterator<LocalDate> {
        var current = start
        override fun hasNext(): Boolean =
            current <= endInclusive

        override fun next(): LocalDate = current.apply {
            current = plusDays(1)
        }
    }

/**
 * 4. 구조 분해 선언과 component 함수
 * - 복합적인 값을 분해해서 여러 다른 변수를 한꺼번에 초기화 가능
 * - componentN이라는 함수 호출, N은 변수 위치에 따라 변하는 번호
 * - 배열이나 컬렉션에도 componentN 함수가 있음, 크기가 정해진 컬렉션을 다룰 때 유용
 * - 맨 앞 다섯 원소에 대한 componentN 제공
 * - Pair나 Triple을 사용하면 여러 값을 더 간단하게 변환 가
 */

val p = Point(10, 20)
val (x, y) = p
println(x)
println(y)

class Point2(val x: Int, val y: Int) {
    operator fun component1() = x
    operator fun component2() = y
}

/**
 * 4.1. 구조 분해 선언과 루프
 * - 변수 선언이 들어갈 수 있는 장소면 어디든 구조 분해 선언 사용 가능
 * - 맵의 원소에 대해 이터레이션할 때 구조 분해 선언이 유용
 * - Map.Entry에 대한 확장 함수로 component1과 component2를 제공
 */

/**
 * 5. 프로퍼티 접근자 로직 재활용: 위임 프로퍼티
 * - 값을 뒷받침하는 필드(배킹필드)에 단순히 저장하는 것보다 더 복잡한 방식으로 작동하는 프로퍼티를 쉽게 구현 가능
 * - 접근자 로직을 매번 재구현할 필요 없음
 * - 값을 필드가 아니라 데이터베이스 테이블이나 브라우저 세션, 맵 등에 저장 가능
 * - 위임은 객체가 직접 작업을 수행하지 않고 다른 도우미 객체가 그 작업을 처리하게 맡기는 디자인 패턴
 * - 도우미 객체를 위임 객체(delegate)라고 부름
 *
 * 5.1. 위임 프로퍼티 소개
 * - p 프로퍼티는 접근자 로직을 다른 객체에게 위임, 여기서는 Delegate 클래스의 인스턴스
 * - by 뒤에 있는 식을 계산해서 위임에 쓰일 객체를 얻음
 * - p 프로퍼티는 숨겨진 도우미 프로퍼티를 만들고 그 프로퍼티를 위임 객체의 인스턴스로 초기화
 * - 프로퍼티의 위임 관례를 따르는 Delegate 클래스는 getValue와 setValue 메소드를 제공
 *
 * 5.2. 위임 프로퍼티 사용: by lazy()를 사용한 프로퍼티 초기화 지연
 * - 지연 초기화는 객체 일부분을 초기화하지 않고 남겨뒀다가 실제로 그 부분의 값이 필요할 경우 초기화할 때 흔히 쓰이는 패턴
 * - 초기화 과정에 자원을 많이 사용하거나 객체를 사용할 때마다 꼭 초기화하지 않아도 되는 프로퍼티에 대해 지연 초기화 사용 가능
 * - 뒷받침하는 프로퍼티(backing property) 기법
 * - 코드 만들기가 성가시고, 스레드 안전 X -> 위임 프로퍼티 사용(배킹 프로퍼티가 오직 한 번만 초기화됨을 보장)
 * - lazy는 기본적으로 스레드 안전, 필요에 따라 락을 함수에 전달할 수도 있고, lazy가 동기화를 하지 못하게 막을 수도 있음
 */

//class Foo {
//    var p: Type by Delegate()
//}

//class Delegate {
//    operator fun getValue(...) { ... }
//    operator fun setValue(..., value: Type) { ... }
//}

//class Person2(val name: String) {
//    private var _emails: String? = null
//    val emails: String
//        get() = {
//            if(_emails == null) {
//                _emails = load(this)
//            }
//            return _emails!!
//        }
//}

// val emails by lazy { load(this) }

/**
 * 5.3. 위임 프로퍼티 구현
 * - 프로퍼티가 바뀔 때마다 리스너에게 변경 통지
 */

open class PropertyChangeAware {
    protected val changeSupport = PropertyChangeSupport(this)
    fun addPropertyChangeListener(listner: PropertyChangeListener) {
        changeSupport.addPropertyChangeListener(listner)
    }
    fun removePropertyChangeListener(listener: PropertyChangeListener) {
        changeSupport.removePropertyChangeListener(listener)
    }
}

class Person3(val name: String, age: Int, salary: Int) : PropertyChangeAware() {
    var age: Int = age
        set(newValue) {
            val oldValue = field
            field = newValue
            changeSupport.firePropertyChange("age", oldValue, newValue)
        }
    var salary: Int = salary
        set(newValue) {
            val oldValue = field
            field = newValue
            changeSupport.firePropertyChange("salary", oldValue, newValue)
        }
}

val p3 = Person3("Dmitry", 34, 2000)
p3.addPropertyChangeListener(PropertyChangeListener { event ->
        println("Property ${event.propertyName} changed from ${event.oldValue} to ${event.newValue}")
    })

p3.age = 35
// Property age changed from 34 to 35
p3.salary = 2100
// Property salary changed from 2000 to 2100

class ObservableProperty(val propName: String, var propValue: Int, val changeSupport: PropertyChangeSupport) {
    fun getValue(): Int = propValue
    fun setValue(newValue: Int) {
        val oldValue = propValue
        propValue = newValue
        changeSupport.firePropertyChange(propName, oldValue, newValue)
    }
}

class Person4(val name: String, age: Int, salary: Int) : PropertyChangeAware() {
    // 각 프로퍼티마다 ObservableProperty를 만들고 게터/세터에서 작업을 위임하는 준비 코드가 필요
    val _age = ObservableProperty("age", age, changeSupport)
    var age: Int
        get() = _age.getValue()
        set(value) { _age.setValue(value) }
    val _salary = ObservableProperty("salary", salary, changeSupport)
    var salary: Int
        get() = _salary.getValue()
        set(value) { _salary.setValue(value) }
}

// -> 코틀린 위임 프로퍼티를 사용하면 준비 코드 제거 가능
// ObservableProperty에 있는 두 메소드를 코틀린 관례에 맞게 수정 필요
class ObservableProperty2(var propValue: Int, val changeSupport: PropertyChangeSupport) {
    // 프로퍼티가 포함된 객체(Person5 타입)과 프로퍼티를 표현하는 객체를 파라미터로 받음
    // KProperty 타입의 객체를 사용해 프로퍼티 표현, KProperty.name을 통해 메소드가 처리할 프로퍼티 이름을 알 수 있음
    operator fun getValue(p: Person5, prop: KProperty<*>): Int = propValue
    operator fun setValue(p: Person5, prop: KProperty<*>, newValue: Int) {
        val oldValue = propValue
        propValue = newValue
        changeSupport.firePropertyChange(prop.name, oldValue, newValue)
    }
}

class Person5(val name: String, age: Int, salary: Int) : PropertyChangeAware() {
    var age: Int by ObservableProperty2(age, changeSupport)
    var salary: Int by ObservableProperty2(salary, changeSupport)
}

// 코틀린 표준랑이브러리를 사용해 관찰 가능한 프로퍼티 로직을 구현 가능ㅇ
class Person6(val name: String, age: Int, salary: Int) : PropertyChangeAware() {
    private val observer = {
        prop: KProperty<*>, oldValue: Int, newValue: Int ->
        changeSupport.firePropertyChange(prop.name, oldValue, newValue)
    }

    // by 오른쪽 식이 꼭 새 인스턴스를 만들 필요는 없고, 함수 호출, 다른 프로퍼티, 다른 식 등을 사용 가능
    // 다만 우항의 식을 계산한 결과 객체는 getValue, setValue를 반드시 제공해야 함
    var age: Int by Delegates.observable(age, observer)
    var salary: Int by Delegates.observable(salary, observer)
}

/**
 * 5.4. 위임 프로퍼티 컴파일 규칙
 * - 컴파일러는 모든 프로퍼티 접근자 안에 getValue와 setValue 호출 코드를 생성
 * - 매커니즘이 단순하지만 프로퍼티 값이 저장될 장소(맵, 데이터베이스, 세션 쿠키)를 바꾼다던지,
 * 프로퍼티를 읽고 쓸 때 벌어질 일(값 검증, 변경 통지)을 변경도 가능
 *
 * 5.5. 프로퍼티 값을 맵에 저장
 * - 자신의 프로퍼티를 동적으로 정의할 수 있는 객체를 만들 때 위임 프로퍼티를 활용하는 경우
 * - 확장 가능한 객체(expando object)라고 부름
 * - 연락처를 예로 들면 일부 필수 정보(이름)가 있고 사람마다 달라질 수 있는 추가 정보(제일 어린 자식의 생일 등)가 있음
 * - 정보를 모두 맵에 저장하되 그 맵을 통해 처리하는 프로퍼티를 통해 필수 정보를 제공
 */

class Person7 {
    private val _attributes = hashMapOf<String, String>()

    fun setAttribute(attrName: String, value: String) {
        _attributes[attrName] = value
    }

    val name: String
        get() = _attributes["name"]!!
}

// 위임 프로퍼티를 활용 가능
class Person8 {
    private val _attributes = hashMapOf<String, String>()

    fun setAttribute(attrName: String, value: String) {
        _attributes[attrName] = value
    }

    // 표준 라이브러리에서 Map과 MutableMap 인터페이스에 대해 getValue, setValue 확장 함수 제공
    val name: String by _attributes
}

/**
 * 5.6. 프레임워크에서 위임 프로퍼티 활용
 * - 데이터베이스에 User라는 테이블이 있고 그 테이블에는 name이라는 문자열 타입의 컬럼과 age라는 정수 타입의 열이 있다고 가정
 */

//object Users : IdTable() { // 객체는 데이터베이스 테이블에 해당
//    // 프로퍼티는 테이블 컬럼
//    val name = varchar("name", length = 50).index
//    val age = integer("age")
//}
//
//class User(id: EntityID) : Entity(id) {
//    var name: String by Users.name
//    var age: Int by Users.age
//}
//
//operator fun <T> Column<T>.getValue(o: Entity, desc: KProperty<*>): T {
//    // 데이터베이스에서 컬럼 값 가져오기
//}
//
//operator fun <T> Column<T>.setValue(o: Entity, desc: KProperty<*>): T {
//    // 데이터베이스의 값 변경하기
//}