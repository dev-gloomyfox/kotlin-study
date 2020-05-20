/**
 * 클래스, 객체, 인터페이스
 *
 * 요약
 * - 클래스와 인터페이스
 * - 뻔하지 않은 생성자와 프로퍼티
 * - 데이터 클래스
 * - 클래스 위임
 * - object 키워드 사용
 */

/**
 * 1. 클래스 계층 정의
 * - 클래스 정의 방식
 * - 가시성과 접근 변경자
 * - sealed
 */

/**
 * 1.1. 코틀린 인터페이스
 * - 추상 메소드 뿐 아니라 구현이 있는 메소드도 정의 가능
 * - 인터페이스에는 상태(필드)는 들어갈 수 없음
 * - 클래스 이름 뒤에 : 을 붙이고 인터페이스와 클래스 이름을 적는 것으로 확장과 구현을 모두 처리
 * - 인터페이스는 개수 제한 없이 구현 가능, 클래스는 하나만 확장 가능
 * - override 변경자는 반드시 사용해야 함, 실수로 상위 클래스 메소드 오버라이드 방지
 * - 인터페이스 메소드도 기본 구현 제공 가능, 메소드 본문을 시그니처 뒤에 추가
 */
interface Clickable {
    fun click()
    fun showOff() = println("I'm clickable!")
}

class Button : Clickable {
    override fun click() {
        println("I was clicked")
    }
}

Button().click()
Button().showOff()

/**
 * - 동일한 메소드를 구현하는 다른 인터페이스 정의 시
 * 두 인터페이스를 함께 구현하면 showOff 구현을 대체할 오버라이딩 메소드 직접 작성 필요
 */

interface Focusable {
    fun setFocus(b: Boolean) =
        println("I ${if (b) "got" else "lost"} focus.")
    fun showOff() = println("I'm focusable!")
}

class Button2 : Clickable, Focusable {
    override fun click() {
        println("I was clicked")
    }

    override fun showOff() {
        super<Clickable>.showOff() // 상위 타입 이름을 <>에 넣어 super를 지정하면
        super<Focusable>.showOff() // 어떤 상위 타입 멤버 메소드 호출인지 지정 가능
    }
}

val button = Button2()
button.showOff()
button.setFocus(true)
button.click()

/**
 * 1.2. open, final, abstract 변경자: 기본적으로 final
 * - 취약한 기반 클래스 문제 해결을 위해 기본이 final
 * - 의도된 클래스와 메소드가 아니면 모두 final로 하여 잘못된 사용을 방지
 * - 상속을 허용하려면 open 변경자 필요
 * - 오버라이드를 허용하고 싶은 메소드나 프로퍼티 앞에도 open 변경자가 필요
 * - 오버라이드한 메소드는 기본적으로 열려있음, 막으려면 final 명시 필요
 * - 많은 경우에 스마트 캐스트가 가능하다는 이점
 *  - 타입 검사 뒤에 변경될 수 없는 변수에만 적용 가능한데,
 *    프로퍼티가 final이 아니면 상속하면 요구 사항을 깰 수 있음
 */

open class RichButton : Clickable {
    fun disable() {}
    open fun animate() {}
    final override fun click() {}
}

/**
 * - abstract 선언 가능, 인스턴스화 불가능, 하위 클래스에서 추상 멤버를 오버라이드 필요
 * - 추상 멤버는 항상 열려 있어서 open 명시 필요없음
 */

abstract class Animated {
    abstract fun animate()
    open fun stopAnimating() {

    }
    fun animateTwice() {

    }
}

/**
 * - 인터페이스 멤버는 항상 열려있고 final로 변경 불가능
 * - 본문이 없으면 자동으로 추상 멤버가 되지만 따로 멤버 선언 앞에 abstract 키워드 필요 없음
 */

/**
 * 1.3. 가시성 변경자: 기본적으로 public
 * - 패키지 전용 가시성은 없음, 패키지를 네임스페이스 관리 용도로만 사용하기 때문
 * - internal이라는 새로운 가시성 변경자 도입, 모듈 내부에서만 볼 수 있음
 * - 최상위 선언에 대해 private 가시성을 허용, 해당 파일 내부에서만 사용 가능
 * - protected는 최상위 선언에 적용 불가능
 * - 어떤 클래스의 기반 타입 목록에 들어있는 타입이나 제네릭 클래스의 타입 파라미터에 들어있는 타입 가시성은 그 클래스 자신의 가시성과 같거나
 * 더 높아야 하고, 메소드 시그니처에 사용된 모든 타입의 가시성은 그 메소드의 가시성과 같거나 더 높아야 함
 * - protected 멤버는 어떤 클래스나 그 클래스를 상속한 클래스 안에서만 보임
 * - internal은 자바에서 보여질 땐 이름을 보기 나쁘게 바꿈, 접근은 가능
 * - 외부 클래스가 내부 클래스나 중첩 클래스의 private 멤버에 접근 불가능
 */

/**
 * 1.4. 내부 클래스와 중첩된 클래스: 기본적으로 중첩 클래스
 * - 도우미 클래스를 캡슐화하거나 코드 정의를 그 코드를 사용하는 곳 가까이에 두고 싶을 때 유용
 * - 중첩 클래스는 명시적으로 요청하지 않으면 바깥쪽 클래스 인스턴스에 대한 접근 권한이 없음
 * - 코틀린 중첩 클래스에 아무런 변경자가 붙지 않으면 자바 static 중첩 클래스와 같음
 * - 바깥쪽 클래스에 대한 참조를 포함하게 만들고 싶으면 inner 변경자를 붙여야 함
 * - 클래스 계층을 만들되 그 계층에 속한 클래스 수를 제한하고 싶은 경우 중첩 클래스 사용
 */

class Outer {
    inner class Inner {
        fun getOuterReference(): Outer = this@Outer
    }
}

/**
 * 1.5. 봉인된 클래스: 클래스 계층 정의 시 계층 확장 제한
 * - when 식의 else 분기 제거, 있을 경우 하위 클래스를 추가해도 모든 분기를 타는지 검사 불가능
 * - 이런 문제를 sealed 클래스로 해결
 * - 상위 클래스에 sealed 변경자를 붙이면 상위 클래스를 상속한 하위 클래스 정의를 제한 가능
 * - sealed 클래스의 하위 클래스를 정의할 땐 반드시 상위 클래스 안에 중첩 필요
 * - when 식에서 sealed 클래스의 모든 하위 클래스를 처리하면 디폴트 분기가 필요 없음
 * - sealed로 표시된 클래스는 open
 * - 내부적으로 Expr 클래스는 private 생성자를 가짐, 해당 생성자는 클래스 내부에서만 호출 가능
 * - sealed 인터페이스는 정의 불가능, 자바쪽 구현을 막을 방법이 없음
 * - 1.1부터는 같은 파일의 아무데서나 sealed 클래스를 상속한 하위 클래스를 만들 수 있고, 데이터 클래스로 하위 클래스 정의도 가능
 */

sealed class Expr {
    class Num(val value: Int) : Expr()
    class Sum(val left: Expr, val right: Expr) : Expr()
}

fun eval(e: Expr): Int =
    when (e) {
        is Expr.Num -> e.value
        is Expr.Sum -> eval(e.right) + eval(e.left)
    }


/**
 * 2. 뻔하지 않은 생성자와 프로퍼티를 갖는 클래스 선언
 * 2.1. 클래스 초기화: 주 생성자와 초기화 블록
 * - constructor는 주 생성자나 부 생성자 정의를 시작할 때 사용
 * - init 키워드는 초기화 블록을 시작, 초기화 블록은 클래스의 객체가 만들어질 때 실행될 초기화 코드가 들어감
 * - 초기화 블록은 주 생성자와 함께 사용
 */

class User constructor(_nickname: String) {
    val nickname: String
    init {
        nickname = _nickname
    }
}

/**
 * 2.2. 부 생성자: 상위 클래스를 다른 방식으로 초기화
 * - 인자에 대한 디폴트 값은 부 생성자로 하지 않음, 여러 방법으로 인스턴스 초기화를 제공해야 할 때 사용
 */


open class MyView {
    constructor(string: String) {

    }

    constructor(string: String, int: Int) {

    }
}

/**
 * 2.3. 인터페이스에 선언된 프로퍼티 구현
 * - 인터페이스에 추상 프로퍼티 선언 삽입 가능
 * - 게터와 세터가 있는 프로퍼티 선언도 가능
 */

interface User2 {
    val email: String
    val nickname: String
        get() = email.substringBefore('@') // nickname은 오버라이드하지 않고 구현 가능
}

/**
 * 2.4. 게터와 세터에서 뒷받침하는 필드에 접근
 * - 어떤 값을 저장하되 그 값을 변경하거나 읽을 때마다 정해진 로직을 실행하는 유형의 프로퍼티
 * - field라는 식별자를 통해 뒷받침하는 필드에 접근 가능
 */

class User3(val name: String) {
    var address: String = "unspecified"
        set(value: String) {
            println("""
                Address was changed for $name:
                "$field" -> "$value"
            """.trimIndent())
            field = value
        }
        get() {
            println("get!")
            return field
        }
}

/**
 * 2.5. 접근자의 가시성 변경
 * - set이나 get에 접근자를 붙일 수 있음
 */

class LengthCounter {
    var counter: Int = 0
        private set
    fun addWord(word: String) {
        counter += word.length
    }
}

/**
 * 3. 컴파일러가 생성한 메소드: 데이터 클래스와 클래스 위임
 * 3.1. 모든 클래스가 정의해야하는 메소드
 * - toString(), equals(), hashCode()
 * - == 동등성 연산, 참조 레퍼런스가 같은 것을 보려면 ===
 *
 * 3.2. 데이터 클래스: 모든 클래스가 정의해야 하는 메소드 자동 생성
 * - toString(), equals(), hashCode()를 생성
 * - copy()라는 불변성을 유지 시켜주는 메소드도 지원
 *
 * 3.3 클래스 위임: by 키워드 사용
 * - 상속을 허용하지 않는 클래스에 새로운 동작 추가 필요
 * - 데코레이터 패턴, 새로운 클래스를 만들되 기존 클래스와 같은 인터페이스를 데코레이터가 제공하고, 기존 클래스를 데코레이터 내부에 필드로 유지
 * - 준비 코드가 많이 필요
 * - by 키워드로 해결 가능
 * */

// Mutablecollection의 구현을 innerSet에 위임
class CountingSet<T>(val innerSet: MutableCollection<T> = HashSet<T>()) : MutableCollection<T> by innerSet {
    var objectsAdded = 0
    override fun add(element: T): Boolean { // 이 메소드는 위임하지 않고 새로운 구현 제공
        objectsAdded++
        return innerSet.add(element)
    }
}

/**
 * 4. object 키워드: 클래스 선언과 인스턴스 생성
 * - 모든 경우에 클래스를 정의하면서 동시에 인스턴스를 생성
 * - 싱글턴
 * - 동반 객체: 인스턴스 메소드는 아니지만 어떤 클래승와 관련 있는 메소등와 팩토리 메소드를 담을 때 사용
 * - 무명 내부 클래스
 *
 * 4.1. 객체 선언: 싱글턴
 * - 단일 인스턴스
 * - 사용될 때 최초 인스턴스화
 * - 프레임워크를 사용하기 위해 특정 인터페이스를 구현해야 하는데, 구현 내부에 다른 상태가 필요하지 않은 경우 유용, 중첩 객체 사용하는 것이 바람직
 * - 자바에서 사용시 INSTANCE 필드 사용
 */

object Payroll {
    fun calculateSalary() {
        println("calc salary")
    }
}

Payroll.calculateSalary()

/**
 * 4.2. 동반 객체: 팩토리 메소드와 정적 멤버가 들어갈 장소
 * - static 키워드가 없음, 최상위 함수와 object 선언 활용
 * - 대부분 최상위 함수 권장, 하지만 class 내부 private 멤버 접근이 필요한 경우 중첩 object 선언의 멤버 함수로 정의 필요
 * - 대표적으로 팩토리 메소드
 * - companion object 키워드 사용
 * - private 생성자를 호출하기 좋은 위치, 자신을 둘러싼 클래스의 private 멤버 접근 가능
 */

class A {
    companion object {
        fun bar() {
            println("companion called")
        }
    }
}

A.bar()

/**
 * 4.3. 동반 객체를 일반 객체처럼 사용
 * - 동반 객체는 클래스 안에 정의된 일반 객체, 동반 객체에 이름을 붙이거나, 인터페이스 상속, 프로퍼티, 확장 함수 정의 가능
 * - 이름을 지정하지 않으면 Companion
 *
 * 4.4. 무명 내부 클래스를 다른 방식으로 작성
 * - 코틀린 무명 클래스는 여러 인터페이스를 구현하거나 확장 가능
 * - 싱글턴이 아님, 객체 식이 쓰일 때마다 새 인스턴스 생성
 * - final이 아닌 변수도 객체 식 내부에서 사용 가능
 * - 여러 메소드를 오버라이드 하는 경우 유용, 메소드가 하나뿐인 인터페이스를 구현하면 람다 사용이 나음
 */