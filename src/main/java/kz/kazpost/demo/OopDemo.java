package kz.kazpost.demo;

import kz.kazpost.model.Person;
import kz.kazpost.model.Recipient;
import kz.kazpost.model.Sender;
import kz.kazpost.strategy.DeliveryStrategy;
import kz.kazpost.strategy.ExpressDeliveryStrategy;
import kz.kazpost.strategy.StandardDeliveryStrategy;

/**
 * Console demonstration of the four OOP principles used in this project.
 */
public final class OopDemo {
    private OopDemo() {
    }

    public static void run() {
        System.out.println("\n========== OOP ҚАҒИДАЛАРЫНЫҢ ДЕМОНСТРАЦИЯСЫ ==========");

        Sender sender = new Sender("Айдана Ермекова", "+7 700 111 22 33", "Алматы қаласы");
        Recipient recipient = new Recipient("Нұрлан Серік", "+7 701 222 33 44", "Астана қаласы");

        System.out.println("\n1. ИНКАПСУЛЯЦИЯ");
        System.out.println("Person класының өрістері private, мәліметтер getter әдістері арқылы алынады:");
        System.out.println("Аты-жөні: " + sender.getFullName());
        System.out.println("Телефон: " + sender.getPhone());
        System.out.println("Мекенжай: " + sender.getAddress());
        System.out.println("Бұл өрістерге сырттан тікелей қол жеткізуге болмайды.");

        System.out.println("\n2. МҰРАГЕРЛІК");
        System.out.println("Sender класының ата-анасы: " + sender.getClass().getSuperclass().getSimpleName());
        System.out.println("Recipient класының ата-анасы: " + recipient.getClass().getSuperclass().getSimpleName());
        System.out.println("Екі класс та Person абстрактілі класынан мұра алады.");

        System.out.println("\n3. АБСТРАКЦИЯ ЖӘНЕ ПОЛИМОРФИЗМ");
        Person[] people = {sender, recipient};
        for (Person person : people) {
            System.out.println(person.getRoleLabel() + ": " + person.getFullName());
        }
        System.out.println("Person — абстрактілі класс; getRoleLabel() әдісін әр туынды класс өзінше орындайды.");

        System.out.println("\n4. ИНТЕРФЕЙС АРҚЫЛЫ ПОЛИМОРФИЗМ");
        DeliveryStrategy strategy = new StandardDeliveryStrategy();
        System.out.println("Интерфейс арқылы таңдалған іске асыру: " + strategy.getName());
        strategy = new ExpressDeliveryStrategy();
        System.out.println("Сол интерфейстің басқа іске асырылуы: " + strategy.getName());

        System.out.println("\n======================================================");
    }
}
