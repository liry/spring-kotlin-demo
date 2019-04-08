package cz.liry.springkotlindemo

import org.springframework.boot.ApplicationRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.builder.SpringApplicationBuilder
import org.springframework.context.support.beans
import org.springframework.jdbc.core.JdbcOperations
import org.springframework.jdbc.core.queryForObject
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@SpringBootApplication
class SpringKotlinDemoApplication

fun main(args: Array<String>) {
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

@RestController
class CustomerRestController(private val customerService: CustomerService) {

    @GetMapping("/customers")
    fun customers() = customerService.all()

    @PostMapping("/customers")
    fun addCustomer(@RequestBody customer: Customer) {
        customerService.insert(customer)
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
