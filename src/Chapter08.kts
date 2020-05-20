import java.util.concurrent.locks.Lock

/**
 * 고차 함수: 파라미터와 반환 값으로 람다 사용
 * - 함수 타입
 * - 고차 함수와 코드를 구조화할 때 고차 함수를 사용하는 방법
 * - 인라인 함수
 * - 비로컬 return과 레이블
 * - 무명 함수
 *
 * 1. 고차 함수 정ㅇ의
 * - 다른 함수를 인자로 받거나 함수를 반환하는 함수
 */
val list = listOf<Int>()
list.filter { it > 0}

/**
 * 1.1. 함수 타입
 * - 함수 파라미터의 타입을 괄호 안에 넣고, 그 뒤에 화살표(->)를 추가한 다음, 함수의 반환 타입을 지정
 */

val sum: (Int, Int) -> Int = { x, y -> x + y }
val action: () -> Unit = { println(42) }

/**
 * 1.2. 인자로 받은 함수 호출
 * - 함수 이름 뒤에 괄호를 붙이고 괄호 안에 원하는 인자를 콤마로 구분해 넣음
 */

fun twoAndThree(operation: (Int, Int) -> Int) {
    val result = operation(2, 3)
    println("The result is $result")
}

/**
 * 1.3. 자바에서 코틀린 함수 타입 사용
 * - 컴파일 코드 안에서 함수 타입은 일반 인터페이스로 변경
 * - FunctionN 인터페이스를 구현하는 객체를 저장
 * - 각 인터페이스에는 invoke 메소드 정의가 있음
 * - 자바 8 이전의 자바에서는 필요한 FunctionN 인터페이스의 invoke 메소드를 구현하는 무명 클래스를 넘기면 됨
 *
 * 1.4. 디폴트 값을 지정한 함수 타입 파라미터나 널이 될 수 있는 함수 타입 파라미터
 * - 매번 람다를 넘기면 기본 동작으로 충분한 대부분의 경우 함수 호출을 오히려 더 불편하게 함
 * - 함수 타입의 파라미터에 대한 디폴트 값을 사용하면 문제를 해결 가능
 * - 널이 될 수 있는 함수 타입을 사용할 수도 있음
 */

//fun <T> Collection<T>.joinToString(
//    separator: String = ", ", prefix: String = "", postfix: String = "",
//    transform: (T) -> String = { it.toString() }
//): String {
//    ...
//}

/**
 * 1.5. 함수를 함수에서 반환
 * - 적절한 로직을 선택해서 함수로 반환 가능
 */

enum class Delivery { STANDARD, EXPEDITED }
class Order(val itemCount: Int)
fun getShippingCostCalculator(delivery: Delivery): (Order) -> Double {
    if (delivery == Delivery.EXPEDITED) {
        return { order -> 6 + 2.1 * order.itemCount }
    }
    return { order -> 1.2 * order.itemCount }
}

/**
 * 1.6. 람다를 활용한 중복 제거
 * - 함수 타입과 람다 식은 재활용하기 좋은 코드를 만들 때 쓸 수 있는 훌륭한 도구
 */

data class SiteVisit(val path: String, val duration: Double, val os: OS)
enum class OS { WINDOWS, LINUX, MAC, IOS, ANDROID }

val log = listOf(
    SiteVisit("/", 34.0, OS.WINDOWS),
    SiteVisit("/", 22.0, OS.MAC),
    SiteVisit("/login", 12.0, OS.WINDOWS),
    SiteVisit("signup", 8.0, OS.IOS),
    SiteVisit("/", 16.3, OS.ANDROID)
)

val averageWindowsDuration = log.filter { it.os == OS.WINDOWS }.map(SiteVisit::duration).average()

// 일반 함수를 통해 중복 제거
fun List<SiteVisit>.averageDurationFor(os: OS) = filter { it.os == os }.map(SiteVisit::duration).average()

log.averageDurationFor(OS.IOS)

// 고차 함수를 사용해 중복 제거(입력 조건을 함수화)
fun List<SiteVisit>.averageDurationFor(predicate: (SiteVisit) -> Boolean) =
    filter(predicate).map(SiteVisit::duration).average()

/**
 * 2. 인라인 함수: 람다의 부가 비용 없애기
 * - 람다가 변수를 포획하면 람다가 생성되는 시점마다 새로운 무명 클래스 객체 생성
 * - 명령문보다 비효율적
 * - inline 변경자를 함수에 붙이면 컴파일러는 그 함수를 호출하는 모든 문장을 함수 본문에 해당하는 바이트코드로 바꿔치기 함
 *
 * 2.1. 인라이닝이 작동하는 방식
 * - 함수를 호출하는 코드를 함수를 호출하는 바이트코드 대신에 함수 본문을 번역한 바이트코드로 컴파일
 * - 전달된 람다의 본문도 함께 인라이닝, 무명 클래스로 감싸지 않음
 */

inline fun <T> synchronized(lock: Lock, action: () -> T): T {
    lock.lock()
    try {
        return action()
    } finally {
        lock.unlock()
    }
}

/**
 * - 아래와 같은 경우(람다를 넘기는 대신 함수 타입의 변수를 넘기는 경우) 인라인 함수를 호출하는 코드 위치에서는 변수에 저장된 람다의 코드를 알 수 없음
 * 때문에 람다 본문은 인라이닝되지 않고 synchronized 함수의 본문만 인라이닝
 */

class LockOwner(val lock: Lock) {
    fun runUnderLock(body: () -> Unit) {
        synchronized(lock, body)
    }
}

/**
 * 2.2. 인라인 함수의 한계
 * - 람다를 사용하는 모든 함수를 인라이닝 할 수는 없음
 * - 함수 본문에서 파라미터로 받은 람다를 호출하면, 그 호출을 쉽게 람다 본문으로 변경 가능
 * - 파라미터로 받은 람다를 다른 변수에 저장하고 나중에 그 변수를 사용하면 람다를 표현하는 객체가 어딘가는 존재해야 하기 때문에 인라이닝 불가능
 * - 일반적으로 인라인 함수의 본문에서 람다 식을 바로 호출하거나 람다식을 인자로 전달받아 바로 호출하는 경우에는 인라이닝 가능
 * - 그런 경우가 아니라면 컴파일러가 Illegal usage of inline-parameter 메시지와 함께 인라이닝 금지
 * - 둘 이상의 람다를 인자로 받는 함수에서 일부 람다만 인라이닝하고 싶을 때도 있음, 어떤 람다에 너무 많은 코드가 들어가거나 인라이닝을 하면 안되는 코드가
 * 들어갈 가능성이 있으면 noinline 변경자를 파라미터 앞에 붙여서 인라이닝 금지 가능
 * - 자바에서 호출할 땐 inline 활용 불가능
 */

inline fun foo(inlined: () -> Unit, noinline notInlined: () -> Unit) {

}

/**
 * 2.3. 컬렉션 연산 인라이닝
 * - 코틀린의 filter, map 등의 함수는 인라인 함수
 * - 함수 본문은 인라이닝되고 추가 객체, 클래스 생성은 없음
 * - 연쇄 했을 경우 중간 리스에 추가하고 그 중간 리스트를 활용하는 방식
 * - 처리할 원소가 많아지면 중간 리스트를 사용하는 부가 비용도 커짐
 * - asSequence를 통해 리스트 대신 시퀀스를 사용하면 중간 리스트로 인한 부가 빙용은 줄어드나, 각 중간 시퀀스가 람다를 필드에 저장하는 객체로 표현하기에
 * 람다를 인라인하지 않음
 * - 시퀀스 연산은 람다를 인라이닝하지 않기 때문에 모든 컬렉션 연산에 asSequence를 붙이면 안됨
 *
 * 2.4. 함수를 인라인으로 선언해야 하는 경우
 * - 일반 함수 호출의 경우 JIT 컴파일 과정에서 인라이닝을 지원
 * - 람다를 인자로 받는 함수를 인라이닝하면 이익이 많음
 * - 인라이닝을 통해 없앨 수 있는 부가 비용이 상당함, 함수 호출 비용을 줄일 수 있고 람다를 표현하는 클래스와 람다 인스턴스에 해당하는 객체 생성 필요 없음
 * - 현재 JVM은 함수 호출과 람다를 인라이닝 할 정도로 똑똑하지는 못함
 * - 넌로컬 반환 같은 기능을 사용할 수 있음
 * - 코드 크기에 주의
 *
 * 2.5. 자원 관리를 위해 인라인된 람다 사용
 * - use 함수 사용
 * - 넌로컬 반환을 사용
 *
 *
 * 3. 고차 함수 안에서 흐름 제어
 * 3.1. 람다 안의 return문: 람다를 둘러싼 함수로부터 반환
 * - 람다 안에서 return을 사용하면 람다로부터만 반환되는 것이 아니라 람다를 호출하는 함수가 실행을 끝내고 반환
 * - 자신을 둘러싸고 있는 블록보다 더 바깥에 있는 다른 블록을 반환하게 만드는 return 문을 넌로컬 return이라고 함
 * - return이 바깥쪽 함수를 반환시킬 수 있는 때는 람다를 인자로 받는 함수가 인라인인 경우뿐
 *
 * 3.2. 람다로부터 반환: 레이블을 사용한 return
 * - 로컬 리턴 사용 가능
 * - 실행을 끝내고 싶은 람다식 앞에 레이블을 붙이고 return 키워드 뒤에 레이블을 추가
 * - 레이블을 붙이는 대신 람다를 인자로 받는 인라인 함수의 이름을 레이블로 사용해도 됨
 * - 레이블을 명시하면 함수 이름을 레이블로 쓸 수 없음
 */

//fun lookForAlice(people: List<Person>) {
//    people.forEach label@ {
//        if (it.name == "Alice") return@label
//    }
//    println("Alice might be somewhere")
//}

/**
 * 3.3. 무명 함수: 기본적으로 로컬 return
 * - 무명 함수는 코드 블록을 함수에 넘길 때 사용 가능한 다른 방법
 * - 무명 함수 안에서 레이블이 붙지 않은 식은 무명 함수 자체를 반환시킬 뿐 무명 함수를 둘러싼 다른 함수를 반환시키지 않음
 * - return에 적용된 규칙은 fun 키워드를 사용해 정의된 가장 안쪽 함수를 반환시킨다는 것
 */

// 로컬 반환
//fun lookForAlice2(people: List<Person>) {
//    people.forEach(fun(person) {
//        if (person.name == "Alice") return
//    })
//}

// 넌로컬 반환
//fun lookForAlice3(people: List<Person>) {
//    people.forEach {
//        if (it.name == "Alice") return
//    }
//}