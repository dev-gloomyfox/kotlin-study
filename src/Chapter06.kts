/**
 * 코틀린 타입 시스템
 * - Nullable 타입과 Null 처리 구문
 * - 코틀린 원시 타입 소개 및 자바 타입과의 관계
 * - 코틀린 컬렉션 소개 및 자바 컬렉션과의 관계
 */

/**
 * 1. 널 가능성
 * - NPE를 피하기 위한 코틀린 타입 시스템 특성
 * - NPE 문제를 컴파일 단으로 이동
 *
 * 1.1 널이 될 수 있는 타입
 * - 타입 이름 뒤에 물음표(?)를 붙임
 * - Type? = Type 또는 null을 의미
 * - 물음표가 없는 타입은 null을 저장할 수 없다는 뜻
 * - 변수.메소드() 처럼 메소드를 직접 호출할 수 없음
 * - 널이 될 수 있는 값을 널이 될 수 없는 타입의 변수에 대입 불가
 * - 널이 될 수 있는 타입의 값을 널이 될 수 없는 타입의 파라미터를 받는 함수의 전달 불가
 *
 * 1.2. 타입의 의미
 * - 타입은 분류로 어떤 값이 가능한지와 그 타입에 대해 수행할 수 있는 연산의 종류 결정
 * - 자바에선 null을 제대로 다루지 못함
 * - 실행 시점에서 널이 될 수 있는 타입이나 널이 될 수 없는 타입의 객체는 같음, 실행 시점에 부가 비용이 없음
 *
 * 1.3. 안전한 호출 연산자: ?.
 * - null 검상와 메소드 호출을 한 번의 연산으로 수행
 * - 연쇄 호출 가능, 중간에 null이 있으면 바로 null 반환
 */

val s: String? = ""

if (s != null) s.toUpperCase() else null
s?.toUpperCase() // 위와 동일

class Address(val streetAddress: String, val zipCode: Int, val city: String, val country: String)
class Company(val name: String, val address: Address)
class Person(val name: String, val company: Company?)

fun Person.countryName(): String {
    val country = this.company?.address?.country
    return if (country != null) country else "Unknown"
}

/**
 * 1.4. 엘비스 연산자: ?:
 * - null 대신 사용할 디폴트 값 지
 * - return이나 throw 등의 연산도 식이기 때문에 엘비스 연산자 우항에 연산을 넣을 수 있음
 */

fun Person.countryNameWithElvis(): String {
    return this.company?.address?.country ?: "Unknown"
}

fun printShippingLabel(person: Person) {
    println(person.company?.address ?: throw IllegalArgumentException("No address"))
}

/**
 * 1.5. 안전한 캐스트: as?
 * - as? 연산자는 어떤 값을 지정한 타입으로 캐스트하고 변환할 수 없으면 null을 반환
 * - 캐스트를 수행한 뒤 엘비스 연산자를 사용하는 것이 일반적
 */


class Person2(val firstName: String, val lastName: String) {
    override fun equals(other: Any?): Boolean {
        val otherPerson = other as? Person2 ?: return false
        return otherPerson.firstName == firstName && otherPerson.lastName == lastName
    }
}

/**
 * 1.6. 널 아님 단언
 * - 느낌표를 이중(!!)으로 사용하면 어떤 값이든 널이 될 수 없는 타입으로 강제로 바꿀 수 있음
 * - 널이 나타나면 NPE 발생
 * - 이 값이 널이 아님을 알고 있고, 잘못 생각했다면 예외가 발생하는 것을 감수하겠다는 것
 * - 보통의 경우 권장하지 않음
 * - 예외 파악을 위해 여러 !! 단언문을 한 줄에 함께 쓰는 일을 피해야 함
 *
 * 1.7. let 함수
 * - let 함수를 안전한 호출 연산자와 함께 사용하면 원하는 식을 평가해서 결과가 널인지 검사한 후 그 결과를 변수에 넣는 작업을 한 번에 처리 가능
 * - 흔한 사용 사례는 널이 될 수 있는 값을 널이 아닌 값만 인자로 받는 함수에 넘기는 경우
 * - let을 중첩시켜 처리하면 코드가 복잡해져 알아보기 어려워지므로 모든 값을 if로 한번에 검사한 후 처리하는 것이 나음
 */

fun sendEmailTo(email: String) { }

val email: String? = "..."
email?.let { sendEmailTo(it) }

/**
 * 1.8. 나중에 초기화할 프로퍼티
 * - 객체 인스턴스를 일단 생성한 후 나중에 초기화하는 경우가 많음(프레임워크 등)
 * - 해당 인스턴스의 프로퍼티가 널이 될 수 없는 경우에 초기화가 불가능, 그렇다고 널이 될 수 있게하면 모든 프로퍼티 접근에 ? 또는 !!가 필요
 * - lateinit 변경자를 붙여서 나중에 초기화 가능
 * - 나중에 초기화하는 프로퍼티는 항상 var
 *
 * 1.9. 널이 될 수 있는 타입 확장
 * - 널이 될 수 있는 타입에 대한 확장 함수를 정의하면 null 값을 다루는 강력한 도구로 활용 가능
 * - 어떤 메소드를 호출하기 전에 수신 객체 역할을 하는 변수가 널이 될 수 없다고 보장하는 대신, 직접 변수에 대해 메소드를 호출해도 확장 함수인 메소드가
 * 알아서 널을 처리
 * - 일반 멤버 호출은 객체 인스턴스를 통해 디스패치되므로 그 인스턴스가 널인지 여부를 검사하지 않음
 * - let은 널이 될 수 있는 타입의 값에 대해 호출할 수 있지만 let은 this가 널인지 검사하지 않음, 수신 객체가 널인지 검사하고 싶으면 ? 연산 필수
 *
 * 1.10. 타입 파라미터의 널 가능성
 * - 타입 파라미터 T는 기본적으로 널이 될 수 있음
 * - 타입 파라미터가 널이 아님을 확실히 하려면 널이 될 수 없는 타입 상한을 지정
 */

fun <T: Any> printHashCode(t: T) { /*...*/ }

/**
 * 1.11. 널 가능성과 자바
 * - 널을 표현하는 어노테이션이 달린 경우 연동됨
 * - 어노테이션이 없는 경우 플랫폼 타입(널 관련 정보를 알 수 없는 타입)으로 사용
 * - 플랫폼 타입(Type!)은 사용자가 내용을 알고 직접 처리 필요
 * - 제네릭을 다룰 때 등 모든 경우에 널 검사를 수행하거나 안전한 캐스트를 수행하면 비용도 더들고 실용적이지 않아서 플랫폼 타입 사용
 * - 자바 메소드를 오버라이드할 때 메소드의 파라미터와 반환 타입을 널이 될 수 있는지 없는지를 판단해서 선언 필요
 */

/**
 * 2. 코틀린의 원시 타입
 * 2.1. 원시 타입: Int, Boolean 등
 * - 원시 타입과 래퍼 타입을 구분하지 않음
 * - 래퍼 타입을 구분하지 않아서 원시 타입에도 메소드 호출이 가능
 * - 실행 시점에는 가장 효율적인 방식으로 표현, 대부분의 경우 Int는 자바의 int로 컴파일
 * - 자바 원시 타입의 값은 null이 될 수 없으므로 코틀린에서도 널이 될 수 없는 타입으로 취급 가능
 *
 * 2.2. 널이 될 수 있는 원시 타입: Int?, Boolean? 등
 * - 자바의 래퍼 타입으로 컴파일
 *
 * 2.3. 숫자 변환
 * - 타입을 자동 변환하지 않음
 * - 내장된 변환 메소드 호출 필수
 *
 * 2.4. Any, Any?: 최상위 타입
 * - 원시 타입을 포함한 모든 타입의 조상 타입
 * - java.lang.Object에 있는 다른 메소드(wait, notify 등)는 Any에서 사용 불가, 이런 메소드 사용 시 java.lang.Object로 캐스트 필요
 *
 * 2.5. Unit 타입: 코틀린의 void
 * - 관심을 가질 만한 내용을 전혀 반환하지 않는 함수의 반환 타입으로 Unit 사용
 * - Unit은 모든 기능을 갖는 일반적인 타입이고, void와 달리 Unit을 타입인자로 사용 가능
 * - Unit 타입에 속한 값은 단 하나뿐이며, 그 이름도 Unit
 * - 제네릭 파라미터를 반환하는 함수를 오버라이드하면서 반환 타입을 Unit으로 쓸 때 유용
 */

interface Processor<T> {
    fun process(): T
}

class NoResultProcessor : Processor<Unit> {
    override fun process() {
        //
    }
}

/**
 * 2.6. Nothing 타입: 이 함수를 결코 정상적으로 끝나지 않는다
 * - 테스트 프레임워크의 fail
 * - 함수가 정상적으로 끝나지 않음을 알면 유용, 이런 경우에 Nothing 타입 사용
 * - Nothing 타입은 아무 값도 포함하지 않음, 함수의 반환 타입이나 반환 타입으로 쓰일 타입 파라미터로만 사용 가능
 * - 엘비스 연산자의 우항에 사용해서 전제 조건을 검사 가능
 */

fun fail(message: String): Nothing {
    throw IllegalArgumentException(message)
}

/**
 * 3. 컬렉션과 배열
 * 3.1. 널 가능성과 컬렉션
 * - 타입 인자로 쓰인 타입에 ?를 붙이면 Nullable(List<Int?> 등)
 *
 * 3.2. 읽기 전용과 변경 가능한 컬렉션
 * - kotlin.collections.Collection은 읽기 전용
 * - kotlin.collections.MutableCollection이 변경 가능
 * - 읽기 전용 컬렉션이라고 변경 불가능한 컬렉션일 필요는 없음, 실제로는 어떤 컬렉션 인스턴스를 가르키는 수많은 참조 중 하나일 수 있음
 * - 이런 이유로 읽기 전용 컬렉션이 스레드 안전하지는 않음
 *
 * 3.3. 코틀린 컬렉션과 자바
 * - 모든 컬렉션은 그에 상응하는 자바 컬렉션 인터페이스의 인스턴스
 * - 기본 구조는 java.util 패키지에 있는 자바 컬렉션 인터페이스를 그대로 사용
 * - 변경 가능한 각 인터페이스는 자신과 대응하는 읽기 전용 인터페이스를 확장
 * - 코틀린 상위 타입을 갖는 것 처럼 취급
 * - 자바 메소드에 읽기 전용 Collection을 넘겨도 코틀린 컴파일러가 이를 막을 수 없음
 * - 이런 경우는 Nullable도 같음, 각별한 취급이 필요
 *
 * 3.4. 컬렉션을 플랫폼 타입으로 다루기
 * - 자바에서 선언한 컬렉션 타입의 수를 플랫폼 타입으로 봄
 * - 컬렉션이 널이 될 수 있는지
 * - 컬렉션의 원소가 널이 될 수 있는지
 * - 오버라이드하는 메소드가 컬렉션을 변경할 수 있는지
 * - 위의 경우를 고려하여 다뤄야 함
 *
 * 3.5. 객체의 배열과 원시 타입의 배열
 * - arrayOf 함수에 원소를 넘기면 배열을 만들 수 있음
 * - arrayOfNulls 함수에 정수 값을 인자로 넘기면 모든 원소가 null이고 인자로 넘긴 값과 크기가 같은 배열 생성
 * - Array 생성자는 배열 크기와 람다를 인자로 받아 각 배열 원소를 초기화
 * - toTypedArray로 컬렉션을 배열로 변환 가능
 * - 배열 타입의 타입 인자도 항상 객체 타입, Array<Int> 같은 배열을 선언하면 박싱된 정수의 배열(Integer[])
 * - 원시 타입의 배열이 필요하면 그런 타입을 위한 배열 클래스 사용 필요(IntArray, CharArray ...)
 * - 컬렉션에 사용할 수 있는 모든 확장 함수를 배열에도 제공, 반환 값은 리스트임에 유의
 * */

val letters = Array<String>(26) { i -> ('a' + i).toString() }

val strings = listOf("a", "b", "c")
println("%s %s %s".format(*strings.toTypedArray()))


