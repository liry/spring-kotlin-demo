package cz.liry.springkotlindemo

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.builder.SpringApplicationBuilder
import org.springframework.context.support.beans
import org.springframework.jdbc.core.JdbcOperations
import org.springframework.jdbc.core.queryForObject
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@SpringBootApplication
class SpringKotlinDemoApplication

fun main(args: Array<String>) {
//    runApplication<SpringKotlinDemoApplication>(*args)

    SpringApplicationBuilder()
            .sources(SpringKotlinDemoApplication::class.java)
            .initializers(beans {
                bean {
                    ApplicationRunner {
                        val customerService = ref<CustomerService>()
                        arrayOf("Jirka", "Martin", "Libor")
                                .map { Customer(name = it) }
                                .forEach { customerService.insert(it) }
                        customerService.all().forEach { println(it) }
                    }
                }

            })
            .run(*args)
}

@Component
class SampleDataInitializer : ApplicationRunner {

    @Autowired
    var customerService: CustomerService? = null

    override fun run(args: ApplicationArguments?) {
        customerService?.insert(Customer("tester"))
        customerService?.all()?.forEach { println(it) }
    }
}

@Service
@Transactional
class JdbcCustomerService(private val jdbc: JdbcOperations) : CustomerService {
    override fun all(): Collection<Customer> = jdbc.query("select * from customers") { rs, _ -> Customer(rs.getString("name"), rs.getLong("id")) }

    override fun byId(id: Long): Customer? =
            jdbc.queryForObject("select * from customer where id = ?", id) { rs, _ -> Customer(rs.getString("name"), rs.getLong("id")) }

    override fun insert(c: Customer) {
        this.jdbc.execute("insert into customers(name) values (?)") {
            it.setString(1, c.name)
            it.execute()
        }
    }

}

interface CustomerService {
    fun all(): Collection<Customer>
    fun byId(id: Long): Customer?
    fun insert(c: Customer)
}

data class Customer(val name: String, var id: Long? = null)
