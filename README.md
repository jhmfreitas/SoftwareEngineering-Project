# Adventure Builder [![Build Status](https://travis-ci.com/tecnico-softeng/es19tg_12-project.svg?token=xDPBAaQ2epnFt9PRstYY&branch=develop)](https://travis-ci.com/tecnico-softeng/es19tg_12-project)[![codecov](https://codecov.io/gh/tecnico-softeng/es19tg_12-project/branch/develop/graph/badge.svg?token=bB74DA0VHo)](https://codecov.io/gh/tecnico-softeng/es19tg_12-project)

To run tests execute: mvn clean install

To see the coverage reports, go to <module name>/target/site/jacoco/index.html.

### Rastreabilidade do trabalho

Ordene a tabela por ordem crescente da data de término.

|   Issue id | Owner (ist number)      | Owner (github username) | PRs id (with link)                         |            Date    |  
| ---------- | ----------------------- | ----------------------- | -------------------                        | ------------------ |
|   #167     |        87671            |     jhmfreitas          | #192(https://github.com/tecnico-softeng/es19tg_12-project/pull/192)  #193(https://github.com/tecnico-softeng/es19tg_12-project/pull/193) (FIX)|24/04/2019 |
|    #183    |      87671          |   jhmfreitas                |#193(https://github.com/tecnico-softeng/es19tg_12-project/pull/193)   |    01/05/2019    |
|   #169     |      89264              |     g3n3goncalves       | #192(https://github.com/tecnico-softeng/es19tg_12-project/pull/195)   |    01/05/2019    |
|   #168     |       87693             |   pedroamaralsoares     |#196(https://github.com/tecnico-softeng/es19tg_12-project/pull/196)  |    04/05/2019      |
|   #171     |       87671             |         jhmfreitas      |#197(https://github.com/tecnico-softeng/es19tg_12-project/pull/197)   |         04/05/2019           |
|   #174     |      87668              |     jcja          		 | #198(https://github.com/tecnico-softeng/es19tg_12-project/pull/198)                                           |    06/05/2019         |
|   #170     |       87693             |   pedroamaralsoares     | #199(https://github.com/tecnico-softeng/es19tg_12-project/pull/199)|   06/05/2019     |
|   #173     |      83609              |     inesqmorais   		 | #200(https://github.com/tecnico-softeng/es19tg_12-project/pull/200)                                           |    07/05/2019         |
|   #185     |      87668              |     jcja          		 | #201(https://github.com/tecnico-softeng/es19tg_12-project/pull/201)                                           |    07/05/2019         |               
|   #175     |      83609              |     inesqmorais   		 | #202(https://github.com/tecnico-softeng/es19tg_12-project/pull/202)                                           |    07/05/2019         |
|   #172     |      89264              |     g3n3goncalves       | #203(https://github.com/tecnico-softeng/es19tg_12-project/pull/203)   |    08/05/2019    |
|   #178     |      87668              |     jcja          		 | #205(https://github.com/tecnico-softeng/es19tg_12-project/pull/205) #206(https://github.com/tecnico-softeng/es19tg_12-project/pull/206) (FIX) |    09/05/2019         |
|   #180     |      83609              |     inesqmorais   		 | #207(https://github.com/tecnico-softeng/es19tg_12-project/pull/207)                                           |    09/05/2019         |
|   #188     |        87671            |         jhmfreitas      | #208(https://github.com/tecnico-softeng/es19tg_12-project/pull/208)   |    09/05/2019   |
|   #187     |      89264              |     g3n3goncalves       | #209(https://github.com/tecnico-softeng/es19tg_12-project/pull/209)   |    09/05/2019    |
|   #189     |      87693              |  pedroamaralsoares      | #210(https://github.com/tecnico-softeng/es19tg_12-project/pull/210) #218(https://github.com/tecnico-softeng/es19tg_12-project/pull/218) (FIX) |    10/05/2019      |
|   #176     |      87668              |     jcja          		 | #211(https://github.com/tecnico-softeng/es19tg_12-project/pull/211)                                           |    10/05/2019         |
|   #179     |      83609              |     inesqmorais   		 | #212(https://github.com/tecnico-softeng/es19tg_12-project/pull/212)                                           |    10/05/2019         |
|   #182     |      89264              |     g3n3goncalves       | #213(https://github.com/tecnico-softeng/es19tg_12-project/pull/213)   |    10/05/2019    |
|   #181     |      83609              |     inesqmorais   		 | #214(https://github.com/tecnico-softeng/es19tg_12-project/pull/214)                                           |    10/05/2019         |
|   #184     |      89264              |     g3n3goncalves       | #215(https://github.com/tecnico-softeng/es19tg_12-project/pull/215)   |    10/05/2019    |
|   #190     |      87693              |  pedroamaralsoares      | #218(https://github.com/tecnico-softeng/es19tg_12-project/pull/218) |   10/05/2019    |
|   #186     |      83609              |  	 inesqmorais         | #221(https://github.com/tecnico-softeng/es19tg_12-project/pull/221) |   10/05/2019    |
|   #177     |      87668              |     jcja          		 | #222(https://github.com/tecnico-softeng/es19tg_12-project/pull/222)                                           |    10/05/2019         |



### Infrastructure

This project includes the persistent layer, as offered by the FénixFramework.
This part of the project requires to create databases in mysql as defined in `resources/fenix-framework.properties` of each module.

See the lab about the FénixFramework for further details.

#### Docker (Alternative to installing Mysql in your machine)

To use a containerized version of mysql, follow these stesp:

```
docker-compose -f local.dev.yml up -d
docker exec -it mysql sh
```

Once logged into the container, enter the mysql interactive console

```
mysql --password
```

And create the 6 databases for the project as specified in
the `resources/fenix-framework.properties`.

To launch a server execute in the module's top directory: mvn clean spring-boot:run

To launch all servers execute in bin directory: startservers

To stop all servers execute: bin/shutdownservers

To run jmeter (nogui) execute in project's top directory: mvn -Pjmeter verify. Results are in target/jmeter/results/, open the .jtl file in jmeter, by associating the appropriate listeners to WorkBench and opening the results file in listener context
