import java.lang.Appendable
import java.lang.IllegalArgumentException
import java.util.*

/**
 * 제네릭
 * - 제네릭 함수와 클래스를 정의하는 방법
 * - 타입 소거와 실체화한 타입 파라미터
 * - 선언 지점과 사요 지점 변
 *
 * 1. 제네릭 타입 파라미터
 * - 제네릭스 사용 시 타입 파라미터를 받는 타입 정의 가능
 * - 타입 파라미터를 구체적인 타입 인자로 치환 필요
 * - 코틀린은 제네릭 타입의 타입 인자를 항상 정의
 *
 * 1.1. 제네릭 함수와 프로퍼티
 * - 확장 프로퍼티는 제네릭하게 선언 가능
 *
 * 1.2. 제네릭 클래스 선언
 * - 꺾쇠 기호(<>)를 클래스나 인터페이스 이름 뒤에 붙임
 *
 * 1.3. 타입 파라미터 제약
 * - 어떤 타입을 제네릭 타입의 타입 파라미터에 대한 상한으로 지정하면 그 제네릭 타입을 인스턴스화할 때 사용하는 타입 인자는 반드시 그 상한 타입이거나
 * 그 상한 타입의 하위 타입이어야 함
 * - 상한을 정하면 T 타입 값을 상한 타입의 값으로 취급 가능
 * - 둘 이상의 제약을 가하는 경우 where 구문 사용
 */

// fun <T : Number> List<T>.sum(): T
fun <T> ensureTrailingPeriod(seq: T) where T : CharSequence, T : Appendable {
    if (!seq.endsWith('.')) {
        seq.append('.')
    }
}

/**
 * 1.4. 타입 파라미터를 널이 될 수 없는 타입으로 한정
 * - 항상 널이 될 수 없는 타입만 타입 인자로 받게 만들려면 타입 파라미터에 제약을 가해야 함
 */

class Processor<T : Any> {
    // ...
}

/**
 * 2. 실행 시 제네릭스의 동작: 소거된 타입 파라미터와 실체화된 타입 파라미터
 * - JVM 제네릭스는 보통 타입 소거를 사용해 구현
 * - 실행 시점에 제네릭 클래스의 인스턴스에 타입 인자 정보가 들어있지 않음
 * - 코틀린 타입 소거가 실죵적인 면에서 어떤 영향을 끼치는지 살펴보고 함수를 inline으로 선언하여 이런 제약을 우회할 수 있는지 살펴
 * - 함수를 inline으로 만들면 타입 인자가 지워지지 않게 할 수 있음(이를 실체화(reify)라고 부름)
 *
 * 2.1. 실행 시점의 제네릭: 타입 검사와 캐스트
 * - 제네릭 타입 인자 정보는 런타임에 제거
 * - 제네릭 클래스 인스턴스가 그 인스턴스를 생성할 때 쓰인 타입 인자에 대한 정보를 유지하지 않음
 * - 실행 시점에 어떤 리스트가 문자열로 이뤄졌는지 다른 객체로 이뤄졌는지 검사 불가능
 * - 일반적으로 말하면 is 검사에서 타입 인자로 지정한 타입 검사 불가능
 * - 저장해야 하는 타입 정보의 크기가 줄어서 전반적인 메모리 사용량이 줄어든다는 제네릭 타입 소거의 장점 존재
 * - * 프로젝션을 통해 타입 인자를 명시하지 않는 것 처럼 사용 가능
 */

//fun printSum(c: Collection<*>) {
//    // 리스트에 다른 타입인 경우 as 캐스팅은 성공하나 다른 예외가 나중에 발생
//    val intList = c as? List<Int> ?: throw IllegalArgumentException()
//    println(intList.sum())
//}
//
//fun printSum(c: Collection<*>) {
//    // 이 경우 컴파일 시점에 파악 가능
//    if (c is List<Int>) {
//        println(c.sum())
//    }
//}

/**
 * 2.2. 실체화한 타입 파라미터를 사용한 함수 선언
 * - 제네릭 클래스의 인스턴스가 있어도 그 인스턴스를 만들 때 사용한 타입 인자를 알아낼 수 없어서 본문에서는 호출 시 쓰인 타입 인자를 알 수 없음
 * - 인라인 함수의 타입 파라미터는 실체화되므로 실행 시점에 인라인 함수의 타입 인자를 알 수 잇음
 * - 함수를 인라인으로 만들고 타입 파라미터를 reified로 지정하면 value 타입이 T의 인스턴스인지를 실행 시점에 검사 가능
 */

inline fun <reified T> isA(value: Any) = value is T

/**
 * 2.3. 실체화한 타입 파라미터로 클래스 참조 대신
 * - java.lang.Class 타입 인자를 파라미터로 받는 API에 대한 코틀린 어댑터를 구축하는 경우 실체화한 타입 파라미터를 자주 사용
 */

//inline fun <reified T> loadService() {
//    return ServiceLoader.load(T::class.java)
//}

/**
 * 2.4. 실체화한 타입 파라미터의 제약
 * - 다음 경우에 실체화한 타입 파라미터 사용 가능
 *  - 타입 검사와 캐스팅(is, !is, as, as?)
 *  - 10장에서 설명할 코틀린 리플렉션 API(::class)
 *  - 코틀린 타입에 대응하는 java.lang.Class 얻기(::class.java)
 *  - 다른 함수를 호출 때 타입 인자로 사용
 * - 다음과 같은 일은 할 수 없음
 *  - 타입 파라미터 클래스의 인스턴스 생성
 *  - 타입 파라미터 클래스의 동반 객체 메소드 호출
 *  - 실체화한 타입 파라미터를 요구하는 함수를 호출하면서 실체화하지 않은 타입 파라미터로 받은 타입을 타입인자로 넘기기
 *  - 클래스, 프로퍼티, 인라인 함수가 아닌 함수의 타입 파라미터를 reified로 지정하기
 */

/**
 * 3. 변성: 제네릭과 하위 타입
 * - 변성 개념은 List<String>와 List<Any>와 같이 기저 타입이 같고 타입인자가 다른 여러 타입이 서로 어떤 관계가 있는지 설명하는 개념
 *
 * 3.1. 변성이 있는 이유: 인자를 함수에 넘기기
 * - 어떤 함수가 컬렉션의 원소를 추가하거나 변경한다면 타입 불일치가 생길 수 있어서 Any 대신 String을 넘길 수 없음
 *
 * 3.2. 클래스, 타입, 하위 타입
 * - 어떤 타입 A 값이 필요한 모든 장소에 어떤 타입 B의 값을 넣어도 아무 문제가 없다면 타입 B는 타입 A의 하위 타입
 * - 모든 타입은 자신의 하위 타입
 * - A 타입이 B 타입의 하위 타입이라면 B는 A의 상위 타입
 * - 어떤 값의 타입이 변수 타입의 하위 타입인 경우에만 값을 변수에 대입하게 허용
 * - 간단한 경우 하위 타입은 하위 클래스와 근본적으로 동일
 * - 널이 될 수 있는 타입은 하위 타입과 하위 클래스가 같지 않은 경우르 보여줌
 * - 널이 될 수 없는 타입은 널이 될 수 있는 타입의 하위 타입
 * - 제네릭 타입을 인스턴스화할 때 타입 인자로 서로 다른 타입이 들어가면서 인스턴스 타입 사이의 하위 관계가 성릴하지 않으면
 * 그 제네릭 타입을 무공변(invariant)이라 함, MutableList
 * - A가 B의 하위 타입이면 List<A>는 List<B>의 하위 타입, 이런 클래스나 인터페이스를 공변적(covariant)라고 말함
 *
 * 3.3. 공변성: 하위 타입 관계를 유지(Java에서 T<? extends A> 이런식으로 표
 * - 코틀린에서 제네릭 클래스가 타입 파라미터에 대해 공변적임을 표시하려면 타입 파라미터 이름 앞에 out을 넣어야 함
 * - 클래스의 타입 파라미터를 공변적으로 만들면 함수 정의에 사용한 파라미터 타입과 타입 인자의 타입이 정확히 일치하지 않더라도 그 클래스의 인스턴스를 함수
 * 인자나 반환 값으로 사용 가능
 * - 모든 클래스를 공변적으로 만들 수는 없음, 공변적으로 만들면 안전하지 못한 클래스도 존재
 * - 타입 안정성을 보장하기 위해 공변적 파라미터는 항상 out 위치에만 있어야 함, T 타입의 값을 생산할 수는 있지만 T 타입의 값을 소비할 수는 없음
 * - 타입 파라미터 T에 붙은 out 키워드는 다음 두 가지를 함께 의미
 *  - 공변성: 하위 타입 관계 유지(Producer<Cat>은 Producer<Animal>의 하위 타입
 *  - 사용 제한: T를 아웃 위치에서만 사용 가능
 *  - List<T> 인터페이스는 읽기 전용이기에 T 타입의 원소를 반환하는 get 메소드는 있으나,
 *  T 타입의 값을 추가하거나 리스트에 있는 기존 값을 변경하는 메소드는 없음, 즉 List는 T에 대해 공변적
 *  - 생성자 파라미터는 in, out 어느쪽도 아님
 *  - 변성은 코드에서 위험할 여지가 있는 메소드를 호출할 수 없게 만들어서 제네릭 타입의
 *  인스턴스 역할을 하는 클래스 인스턴스를 잘못 사용하는 일이 없게 방지하는 역할을 함
 *  - 변성 규칙은 외부에서 잘못 사용하는 일을 막기 위한 것이므로 클래스 내부 구현에는 적용되지 않
 */

interface Producer<out T> {
    fun produce(): T
}

/**
 * 3.4. 반공변성: 뒤집힌 하위 타입 관계현(Java에서 T<? super B>
 * - 반공변성(contravariance)은 공변성을 거울에 비친 상이라 할 수 있음
 * - 반공변 클래스의 하위 타입 관계는 공변 클래스의 경우와 반대
 * - T가 in 위치에서만 사용
 * - 타입 B가 타입 A의 하위 타입인 경우 Class<A>가 Class<B>의 하위 타입인 관계가 성립하면 Class<T>는 타입 인자 T에 대해 반공변
 * - 하위 타입 관계가 뒤집힘
 * - in 키워드는 키워드가 붙은 타입이 이 클래스의 메소드 안으로 전달돼 메소드에 의해 소비된다는 뜻
 * - 어떤 타입 파라미터에 대해서는 공변적이면서 다른 타입 파라미터에 대해서는 반공변적일 수 있음, 대표적으로 Function 인터페이스
 * - 클래스 정의에 변성을 직접 기술하면 그 클래스를 사용하는 모든 장소에 변성이 적용, 자바에서는 이를 지원하지 않음, 클래스를 사용하는 위치에서 와일드 카드를
 * 사용해 그때그때 변성을 지정해야 함
 */

//interface Comparator<in T> {
//    fun compare(e1: T, e2: T): Int {...}
//}

interface Function1<in P, out R> {
    operator fun invoke(p: P): R
}

/**
 * 3.5. 사용 지점 변성: 타입이 언급되는 지점에서 변성 지정
 * - 클래스를 선언하면서 변성을 지정하면 그 클래스를 사용하는 모든 장소에 변성 지정자가 영향을 끼치므로 편리, 선언 지점 변성
 * - 자바에서는 타입 파라미터가 있는 타입을 사용할 때마다 해당 타입 파라미터를 하위 타입이나 상위 타입 중 어떤 타입으로 대치할 수 있는 명시, 사용 지점 변성
 * - 코틀린에서도 사용 지점 변성 사용 가능(상당 수 인터페이스가 무공변), 타입 프로젝션이 일어남
 */

// out 키워드를 타입을 사용하는 위치 앞에 붙이면 T 타입을 in 위치에 사용하는 메소드를 호출하지 않는다는 뜻
fun <T> copyData(source: MutableList<out T>, destination: MutableList<T>) {
    for (item in source) {
        destination.add(item)
    }
}

/**
 * 3.6. 스타 프로젝션: 타입 인자 대신 * 사용
 * - 제네릭 타입 인자 정보가 없을 표현
 * - MutalbleList<*>은 MutableList<Any?>와 같지 않음
 * - MutableList<Any?>는 모든 타입의 원소를 담을 수 있다는 사실
 * - MutableList<*>은 어떤 정해진 구체적인 타입의 원소만을 담는 리스트지만 그 원소의 타입을 정확히 모른다는 사실을 표현
 * - 제네릭 타입 파라미터가 어떤 타입인지 굳이 알 필요가 없을 때만 스타 프로젝션을 사용 가능
 * - 스타 프로젝션을 사용할 때는 값을 만들어내는 메소드만 호출할 수 있고 그 값의 타입에는 신경 쓰지 말아야 함
 */