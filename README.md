# 📦 Қазпошта сәлемдемелерін жеткізу және трекинг жүйесі

## 1. Жоба туралы

Бұл жоба Қазпошта сәлемдемелерін тіркеу, жеткізу және олардың қозғалысын бақылау процесін модельдейтін оқу жобасы. Жүйе сәлемдемені тіркеу, бірегей трек-нөмір беру, статусын жаңарту және статус тарихын сақтау функцияларын жобалайды.

**Жоба тақырыбы:** Сәлемдеме жеткізу және трекинг  
**Ұйым:** Қазпошта  
**Тапсырма нұсқасы:** 16  
**Жоспарланған технология:** Java 17+

## 2. Проблема және жобаның мақсаты

Сәлемдеме туралы мәліметтер мен статус тарихын жүйелі түрде сақтау және жеткізу барысын бақылау маңызды. Жобаның мақсаты — сәлемдемелерді тіркеу, трек-нөмірмен іздеу, статусын басқару және өзгерістер тарихын сақтау процесін бір оқу жүйесінде модельдеу.

Бұл жоба Қазпоштаның ішкі жүйесі емес және нақты жөнелтілімдермен байланыспайды.

## 3. Репозиторий құрылымы

```text
Kazpost-Parcel-Tracking/
├── README.md
└── docs/
    ├── research.md
    └── uml/
        ├── use-case.png
        ├── use-case.puml
        ├── class-diagram.png
        ├── class-diagram.puml
        ├── sequence-diagram.png
        ├── sequence-diagram.puml
        ├── activity-diagram.png
        ├── activity-diagram.puml
        ├── state-diagram.png
        ├── state-diagram.puml
        ├── er-diagram.png
        ├── er-diagram.puml
        └── UML-Documentation.md
```

## 4. Зерттеу құжаты

Ұйым туралы зерттеу, анықталған проблема, ұсынылатын шешім, мақсат, міндеттер, функционалдық талаптар және ұқсас жүйелермен салыстыру: [docs/research.md](docs/research.md).

## 5. UML және деректер қоры диаграммалары

Барлық диаграммалардың PNG суреттері мен PlantUML бастапқы файлдары `docs/uml/` ішінде сақталады.

| Диаграмма | Бастапқы файл | PNG суреті |
|---|---|---|
| Use Case | [use-case.puml](docs/uml/use-case.puml) | [use-case.png](docs/uml/use-case.png) |
| Class Diagram | [class-diagram.puml](docs/uml/class-diagram.puml) | [class-diagram.png](docs/uml/class-diagram.png) |
| Sequence Diagram | [sequence-diagram.puml](docs/uml/sequence-diagram.puml) | [sequence-diagram.png](docs/uml/sequence-diagram.png) |
| Activity Diagram | [activity-diagram.puml](docs/uml/activity-diagram.puml) | [activity-diagram.png](docs/uml/activity-diagram.png) |
| State Diagram | [state-diagram.puml](docs/uml/state-diagram.puml) | [state-diagram.png](docs/uml/state-diagram.png) |
| ER Diagram | [er-diagram.puml](docs/uml/er-diagram.puml) | [er-diagram.png](docs/uml/er-diagram.png) |

Диаграммалардың түсіндірмесі: [UML-Documentation.md](docs/uml/UML-Documentation.md).

## 6. Жобалауда ескерілетін негізгі кластар

- `Parcel` — сәлемдеме туралы мәліметтер.
- `Sender` — жіберуші.
- `Recipient` — алушы.
- `PostOffice` — пошта бөлімшесі.
- `TrackingEvent` — статус өзгерісінің тарихы.
- `DeliveryStatus` — жеткізу статустарының `enum` түрі.
- `ParcelService` — сәлемдемелерді басқару логикасы.

Қосымша жобалық элементтер Class Diagram-да көрсетілген: абстрактілі `Person`, `DeliveryStrategy` интерфейсі, Generic `Repository<T>`, DAO және сақтау компоненттері.

## 7. Қазіргі кезеңнің шекарасы

Осы репозиторий құрылымы Research, UML және деректер қоры схемасын жобалау кезеңіне арналған. Java іске асыру, Maven конфигурациясы, CSV/JSON деректері, JUnit тесттері және командалық Branch/Pull Request процесі келесі кезеңдерде қосылады.
