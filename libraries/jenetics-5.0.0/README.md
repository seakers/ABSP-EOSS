# Jenetics

[![Build Status](https://travis-ci.org/jenetics/jenetics.svg?branch=master)](https://travis-ci.org/jenetics/jenetics)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.jenetics/jenetics/badge.svg)](http://search.maven.org/#search|ga|1|a%3A%22jenetics%22)
[![Javadoc](https://www.javadoc.io/badge/io.jenetics/jenetics.svg)](http://www.javadoc.io/doc/io.jenetics/jenetics)
[![Code Quality: Java](https://img.shields.io/lgtm/grade/java/g/jenetics/jenetics.svg?logo=lgtm&logoWidth=18)](https://lgtm.com/projects/g/jenetics/jenetics/context:java)
[![Total Alerts](https://img.shields.io/lgtm/alerts/g/jenetics/jenetics.svg?logo=lgtm&logoWidth=18)](https://lgtm.com/projects/g/jenetics/jenetics/alerts)

**Jenetics** is an **Genetic Algorithm**, **Evolutionary Algorithm** and **Genetic Programming** library, respectively, written in Java. It is designed with a clear separation of the several concepts of the algorithm, e.g. `Gene`, `Chromosome`, `Genotype`, `Phenotype`, `Population` and fitness `Function`. **Jenetics** allows you to minimize and maximize the given fitness function without tweaking it. In contrast to other GA implementations, the library uses the concept of an evolution stream (`EvolutionStream`) for executing the evolution steps. Since the `EvolutionStream` implements the Java Stream interface, it works smoothly with the rest of the Java Stream API.

**Other languages**

* [**Jenetics.Net**](https://github.com/rmeindl/jenetics.net): Experimental .NET Core port in C# of the base library. 
* [**Helisa**](https://github.com/softwaremill/helisa/): Scala wrapper around the Jenetics library.

## Documentation

The library is fully documented ([javadoc](http://jenetics.io/javadoc/jenetics/4.4/index.html)) and comes with an user manual ([pdf](http://jenetics.io/manual/manual-4.4.0.pdf)).


## Requirements

### Runtime
*  **JRE 8**: Java runtime version 8 is needed for using the library, respectively for running the examples.

### Build time
*  **JDK 8**: The Java [JDK 8](http://www.oracle.com/technetwork/java/javase/downloads/index.html) must be installed.
*  **Gradle 5.x**: [Gradle](http://www.gradle.org/) is used for building the library. (Gradle is download automatically, if you are using the Gradle Wrapper script `./gradlew`, located in the base directory, for building the library.)

### Test compile/execution
*  **TestNG 6.x**: Jenetics uses [TestNG](http://testng.org/doc/index.html) framework for unit tests.
*  **Apache Commons Math 3.6**: [Library](http://commons.apache.org/proper/commons-math/) is used for testing statistical collectors.

## Build Jenetics

For building the Jenetics library from source, download the most recent, stable package version from [Github](https://github.com/jenetics/jenetics/releases/download/v4.4.0/jenetics-4.4.0.zip) and extract it to some build directory.

    $ unzip jenetics-<version>.zip -d <builddir>

`<version>` denotes the actual Jenetics version and `<builddir>` the actual build directory. Alternatively you can check out the master branch from Github.

    $ git clone https://github.com/jenetics/jenetics.git <builddir>

Jenetics uses [Gradle](http://www.gradle.org/downloads) as build system and organizes the source into *sub*-projects (modules). Each sub-project is located in it’s own sub-directory:

**Published projects**

The following projects/modules are also published to Maven.

* **[jenetics](jenetics)** [![Javadoc](https://www.javadoc.io/badge/io.jenetics/jenetics.svg)](http://www.javadoc.io/doc/io.jenetics/jenetics): This project contains the source code and tests for the Jenetics core-module.
* **[jenetics.ext](jenetics.ext)** [![Javadoc](https://www.javadoc.io/badge/io.jenetics/jenetics.svg)](http://www.javadoc.io/doc/io.jenetics/jenetics.ext): This module contains additional _non_-standard GA operations and data types. It also contains classes for solving multi-objective problems (MOEA). 
* **[jenetics.prog](jenetics.prog)** [![Javadoc](https://www.javadoc.io/badge/io.jenetics/jenetics.svg)](http://www.javadoc.io/doc/io.jenetics/jenetics.prog): The modules contains classes which allows to do genetic programming (GP). It seamlessly works with the existing `EvolutionStream` and evolution `Engine`.
* **[jenetics.xml](jenteics,xml)** [![Javadoc](https://www.javadoc.io/badge/io.jenetics/jenetics.svg)](http://www.javadoc.io/doc/io.jenetics/jenetics.xml): XML marshalling module for the _Jenetics_ base data structures.

**Non-published projects**

* **jenetics.example**: This project contains example code for the *core*-module.
* **jenetics.doc**: Contains the code of the web-site and the manual.
* **jenetics.tool**: This module contains classes used for doing integration testing and algorithmic performance testing. It is also used for creating GA performance measures and creating diagrams from the performance measures.

For building the library change into the `<builddir>` directory (or one of the module directory) and call one of the available tasks:

* **compileJava**: Compiles the Jenetics sources and copies the class files to the `<builddir>/<module-dir>/build/classes/main` directory.
* **jar**: Compiles the sources and creates the JAR files. The artifacts are copied to the `<builddir>/<module-dir>/build/libs` directory.
* **javadoc**: Generates the API documentation. The Javadoc is stored in the `<builddir>/<module-dir>/build/docs` directory
* **test**: Compiles and executes the unit tests. The test results are printed onto the console and a test-report, created by TestNG, is written to `<builddir>/<module-dir>` directory.
* **clean**: Deletes the `<builddir>/build/*` directories and removes all generated artifacts.

For building the library jar from the source call

    $ cd <build-dir>
    $ ./gradlew jar


## Example

### Hello World (Ones counting)

The minimum evolution Engine setup needs a genotype factory, `Factory<Genotype<?>>`, and a fitness `Function`. The `Genotype` implements the `Factory` interface and can therefore be used as prototype for creating the initial `Population` and for creating new random `Genotypes`.

```java
import io.jenetics.BitChromosome;
import io.jenetics.BitGene;
import io.jenetics.Genotype;
import io.jenetics.engine.Engine;
import io.jenetics.engine.EvolutionResult;
import io.jenetics.util.Factory;

public class HelloWorld {
    // 2.) Definition of the fitness function.
    private static Integer eval(Genotype<BitGene> gt) {
        return gt.getChromosome()
            .as(BitChromosome.class)
            .bitCount();
    }

    public static void main(String[] args) {
        // 1.) Define the genotype (factory) suitable
        //     for the problem.
        Factory<Genotype<BitGene>> gtf =
            Genotype.of(BitChromosome.of(10, 0.5));

        // 3.) Create the execution environment.
        Engine<BitGene, Integer> engine = Engine
            .builder(HelloWorld::eval, gtf)
            .build();

        // 4.) Start the execution (evolution) and
        //     collect the result.
        Genotype<BitGene> result = engine.stream()
            .limit(100)
            .collect(EvolutionResult.toBestGenotype());

        System.out.println("Hello World:\n" + result);
    }
}
```

In contrast to other GA implementations, the library uses the concept of an evolution stream (`EvolutionStream`) for executing the evolution steps. Since the `EvolutionStream` implements the Java Stream interface, it works smoothly with the rest of the Java streaming API. Now let's have a closer look at listing above and discuss this simple program step by step:

1. The probably most challenging part, when setting up a new evolution `Engine`, is to transform the problem domain into a appropriate `Genotype` (factory) representation. In our example we want to count the number of ones of a `BitChromosome`. Since we are counting only the ones of one chromosome, we are adding only one `BitChromosome` to our `Genotype`. In general, the `Genotype` can be created with 1 to n chromosomes.

1. Once this is done, the fitness function which should be maximized, can be defined. Utilizing the new language features introduced in Java 8, we simply write a private static method, which takes the genotype we defined and calculate it's fitness value. If we want to use the optimized bit-counting method, `bitCount()`, we have to cast the `Chromosome<BitGene>` class to the actual used `BitChromosome` class. Since we know for sure that we created the Genotype with a `BitChromosome`, this can be done safely. A reference to the eval method is then used as fitness function and passed to the `Engine.build` method.

1. In the third step we are creating the evolution `Engine`, which is responsible for changing, respectively evolving, a given population. The `Engine` is highly configurable and takes parameters for controlling the evolutionary and the computational environment. For changing the evolutionary behavior, you can set different alterers and selectors. By changing the used `Executor` service, you control the number of threads, the Engine is allowed to use. An new `Engine` instance can only be created via its builder, which is created by calling the `Engine.builder` method.

1. In the last step, we can create a new `EvolutionStream` from our `Engine`. The `EvolutionStream` is the model or view of the evolutionary process. It serves as a »process handle« and also allows you, among other things, to control the termination of the evolution. In our example, we simply truncate the stream after 100 generations. If you don't limit the stream, the `EvolutionStream` will not terminate and run forever. Since the `EvolutionStream` extends the `java.util.stream.Stream` interface, it integrates smoothly with the rest of the Java Stream API. The final result, the best `Genotype` in our example, is then collected with one of the predefined collectors of the `EvolutionResult` class.


### Evolving images

This example tries to approximate a given image by semitransparent polygons.  It comes with an Swing UI, where you can immediately start your own experiments. After compiling the sources with

    $ ./gradlew compileTestJava

you can start the example by calling

    $ ./jrun io.jenetics.example.image.EvolvingImages

![Evolving images](https://raw.githubusercontent.com/jenetics/jenetics/master/jenetics.doc/src/main/resources/graphic/EvolvingImagesExampleScreenShot.png)

The previous image shows the GUI after evolving the default image for about 4,000 generations. With the »Open« button it is possible to load other images for polygonization. The »Save« button allows to store polygonized images in PNG format to disk. At the button of the UI, you can change some of the GA parameters of the example.


## Projects using Jenetics

* <a href="https://renaissance.dev/"><b>Renaissance Suite</b>:</a> Renaissance is a modern, open, and diversified benchmark suite for the JVM, aimed at testing JIT compilers, garbage collectors, profilers, analyzers and other tools.
* <a href="https://www.chartsy.one/"><b>Chartsy|One</b>:</a> Chartsy|One is a Netbeans based tool for stock market investors and traders.
* <a href="http://chronetic.io/"><b>Chronetic</b>:</a> Chronetic is an open-source time pattern analysis library built to describe time-series data.
* <a href="http://www.eclipse.org/app4mc/"><b>APP4MC</b>:</a> Eclipse APP4MC is a platform for engineering embedded multi- and many-core software systems.

## Blogs

* <a href="http://www.baeldung.com/jenetics">Introduction to Jenetics Library</a>, by <em>baeldung</em>, April 11. 2017
* <a href="http://blog.takipi.com/how-to-solve-tough-problems-using-genetic-algorithms/">How to Solve Tough Problems Using Genetic Algorithms</a>, by <em>Tzofia Shiftan</em>, April 6. 2017
* <a href="http://fxapps.blogspot.co.at/2017/01/genetic-algorithms-with-java.html">Genetic algorithms with Java</a>, by <em>William Antônio</em>, January 10. 2017
* <a href="http://jdm.kr/blog/135">Jenetics 설치 및 예제</a>, by <em>JDM</em>, May 8. 2015
* <a href="http://jdm.kr/blog/104">유전 알고리즘 (Genetic Algorithms)</a>, by <em>JDM</em>, April 2. 2015


## Citations

* Aleksandar Prokopec, Andrea Rosà, David Leopoldseder, Gilles Duboscq, Petr Tůma, Martin Studener, Lubomír Bulej, Yudi Zheng, Alex Villazón, Doug Simon, Thomas Würthinger, Walter Binder. <a href="https://renaissance.dev/resources/docs/renaissance-suite.pdf">Renaissance: Benchmarking Suite for Parallel Applications on the JVM. </a> <em>PLDI ’19, Phoenix, AZ, USA. </em></a> June 2019.
* Robert Höttger, Lukas Krawczyk, Burkhard Igel, Olaf Spinczyk. <a href="http://2019.rtas.org/wp-content/uploads/2019/04/RTAS19_BP_proceedings.pdf#page=23">Memory Mapping Analysis for Automotive Systems. </a> <em>Brief Presentations Proceedings (RTAS 2019). </em></a> Apr. 2019.
* Al Akkad, M. A., & Gazimzyanov, F. F. <a href="http://izdat.istu.ru/index.php/ISM/article/view/4317">AUTOMATED SYSTEM FOR EVALUATING 2D-IMAGE COMPOSITIONAL CHARACTERISTICS: CONFIGURING THE MATHEMATICAL MODEL.</a> <em>Intellekt. Sist. Proizv., 17(1), 26-33. doi: 10.22213/2410-9304-2019-1-26-33. </em></a> Apr. 2019.
* Alcayde, A.; Baños, R.; Arrabal-Campos, F.M.; Montoya, F.G. <a href="https://www.mdpi.com/1996-1073/12/7/1270">Optimization of the Contracted Electric Power by Means of Genetic Algorithms.</a> <em>Energies, Volume 12, Issue 7, </em></a> Apr. 2019.
* Aleksandar Prokopec, Andrea Rosà, David Leopoldseder, Gilles Duboscq, Petr Tůma, Martin Studener, Lubomír Bulej, Yudi Zheng, Alex Villazón, Doug Simon, Thomas Wuerthinger, Walter Binder. <a href="https://arxiv.org/pdf/1903.10267.pdf">On Evaluating the Renaissance Benchmarking Suite: Variety, Performance, and Complexity.</a> <em>Cornell University: Programming Languages, </em></a> Mar. 2019.
* S. Appel, W. Geithner, S. Reimann, M Sapinski, R. Singh, D. M. Vilsmeier <a href="https://www.researchgate.net/profile/Sabrina_Appel/publication/330934110_OPTIMIZATION_OF_HEAVY-ION_SYNCHROTRONS_USING_NATURE-INSPIRED_ALGORITHMS_AND_MACHINE_LEARNING/links/5c5c425b299bf1d14cb33546/OPTIMIZATION-OF-HEAVY-ION-SYNCHROTRONS-USING-NATURE-INSPIRED-ALGORITHMS-AND-MACHINE-LEARNING.pdf">OPTIMIZATION OF HEAVY-ION SYNCHROTRONS USINGNATURE-INSPIRED ALGORITHMS AND MACHINE LEARNING.</a><em><a href="https://bt.pa.msu.edu/ICAP18/index.html">13th Int. Computational Accelerator Physics Conf.</a>, </em></a> Feb. 2019.
* Saad, Christian, Bernhard Bauer, Ulrich R Mansmann, and Jian Li. <a href="https://journals.sagepub.com/doi/10.1177/1177932218818458">AutoAnalyze in Systems Biology.</a> <em>Bioinformatics and Biology Insights, </em></a> Jan. 2019.
* Gandeva Bayu Satrya, Soo Young Shin. <a href="https://arxiv.org/pdf/1812.01201.pdf">Evolutionary Computing Approach to Optimize Superframe Scheduling on Industrial Wireless Sensor Networks.</a> <em>Cornell University, </em></a> Dec. 2018.
* H.R. Maier, S. Razavi, Z. Kapelan, L.S. Matott, J. Kasprzyk, B.A. Tolson. <a href="https://www.sciencedirect.com/science/article/pii/S1364815218305905">Introductory overview: Optimization using evolutionary algorithms and other metaheuristics.</a> <em>Environmental Modelling & Software, </em></a> Dec. 2018.
* Erich C. Teppan and Giacomo Da Col. <a href="http://ceur-ws.org/Vol-2252/paper4.pdf">Automatic Generation of Dispatching Rules for Large Job Shops by Means of Genetic Algorithms.</a> <em>CIMA 2018, International Workshop on Combinations of Intelligent Methods and Applications, </em></a> Nov. 2018.
* Pasquale Salzaa, Filomena Ferrucci. <a href="https://www.sciencedirect.com/science/article/pii/S0167739X17324147">Speed up genetic algorithms in the cloud using software containers.</a> <em>Future Generation Computer Systems, </em></a> Oct. 2018.
* Ghulam Mubashar Hassan and Mark Reynolds. <a href="https://easychair.org/publications/open/GRLP">Genetic Algorithms for Scheduling and Optimization of Ore Train Networks.</a> <em>GCAI-2018. 4th Global Conference on Artificial Intelligence, </em></a> Sep. 2018.
* Drezewski, Rafal & Kruk, Sylwia & Makowka, Maciej. [The Evolutionary Optimization of a Company’s Return on Equity Factor: Towards the Agent-Based Bio-Inspired System Supporting Corporate Finance Decisions.](https://ieeexplore.ieee.org/stamp/stamp.jsp?arnumber=8466578) <em>IEEE Access. 6. 10.1109/ACCESS.2018.2870201, </em></a> Sep. 2018.
* Arifin, H. H., Chimplee, N. , Kit Robert Ong, H. , Daengdej, J. and Sortrakul, T. <a href="https://onlinelibrary.wiley.com/doi/abs/10.1002/j.2334-5837.2018.00549.x">Automated Component‐Selection of Design Synthesis for Physical Architecture with Model‐Based Systems Engineering using Evolutionary Trade‐off.</a> <em><a href="https://onlinelibrary.wiley.com/doi/abs/10.1002/j.2334-5837.2018.00549.x">INCOSE International Symposium, 28: 1296-1310</a>, </em></a> Aug. 2018.
* Stephan Pirnbaum. [Die Evolution im Algorithmus - Teil 2: Multikriterielle Optimierung und Architekturerkennung.](http://www.buschmais.de/wp-content/uploads/2018/06/Die-Evolution-im-Algorithmus_Teil2_JS_03_18.pdf) <a href="https://www.sigs-datacom.de/digital/javaspektrum/"><em>JavaSPEKTRUM 03/2018, pp 66–69, </em></a> May 2018.
* W. Geithner, Z. Andelkovic, S. Appel, O. Geithner, F. Herfurth, S. Reimann, G. Vorobjev, F. Wilhelmstötter. [Genetic Algorithms for Machine Optimization in the Fair Control System Environment.](http://accelconf.web.cern.ch/AccelConf/ipac2018/papers/thpml028.pdf)<em> [The 9th International Particle Accelerator Conference (IPAC'18)](https://ipac18.org/welcome/), </em></a> May 2018.
* Stephan Pirnbaum. [Die Evolution im Algorithmus - Teil 1: Grundlagen.](http://www.buschmais.de/wp-content/uploads/2018/02/Die-Evolution-im-Algorithmus_JS_01_18.pdf) <a href="https://www.sigs-datacom.de/digital/javaspektrum/"><em>JavaSPEKTRUM 01/2018, pp 64–68, </em></a> Jan. 2018.
* Alexander Felfernig, Rouven Walter, José A. Galindo, David Benavides, Seda Polat Erdeniz, Müslüm Atas, Stefan Reiterer. <a href="https://link.springer.com/article/10.1007/s10844-017-0492-1">Anytime diagnosis for reconfiguration. </a> <em>Journal of Intelligent Information Systems, pp 1–22, </em> Jan. 2018.
* Bruce A. Johnson. <a href="https://link.springer.com/protocol/10.1007/978-1-4939-7386-6_13">From Raw Data to Protein Backbone Chemical Shifts Using NMRFx Processing and NMRViewJ Analysis. </a> <em>Protein NMR: Methods and Protocols, pp. 257--310, Springer New York, </em> Nov. 2017.
* Cuadra P., Krawczyk L., Höttger R., Heisig P., Wolff C. <a href="https://link.springer.com/chapter/10.1007/978-3-319-67642-5_30">Automated Scheduling for Tightly-Coupled Embedded Multi-core Systems Using Hybrid Genetic Algorithms. </a> <em>Information and Software Technologies: 23rd International Conference, ICIST 2017, Druskininkai, Lithuania.</em> Communications in Computer and Information Science, vol 756. Springer, Cham, Sep. 2017.
* Michael Trotter, Guyue Liu, Timothy Wood. <a href="http://ieeexplore.ieee.org/abstract/document/8064120/">Into the Storm: Descrying Optimal Configurations Using Genetic Algorithms and Bayesian Optimization. </a> <em><a href="http://ieeexplore.ieee.org/xpl/mostRecentIssue.jsp?punumber=8063634">Foundations and Applications of Self* Systems (FAS*W), 2017 IEEE 2nd International Workshops</a></em> Sep. 2017.
* Emna Hachicha, Karn Yongsiriwit, Mohamed Sellami. <a href="https://doi.org/10.1109/ICWS.2017.101">Genetic-Based Configurable Cloud Resource Allocation in QoS-Aware Business Process Development. </a> <em>Information and Software Technologies: 23rd International Conference, ICIST 2017, Druskininkai, Lithuania.</em> Web Services (ICWS), 2017 IEEE International Conference, Jun. 2017.
* Abraão G. Nazário, Fábio R. A. Silva, Raimundo Teive, Leonardo Villa, Antônio Flávio, João Zico, Eire Fragoso, Ederson F. Souza. <a href="http://siaiap32.univali.br/seer/index.php/acotb/article/view/10579/5933">Automação Domótica Simulada Utilizando Algoritmo Genético Especializado na Redução do Consumo de Energia. </a> <em>Computer on the Beach 2017</em> pp. 180-189, March 2017.
* Bandaru, S. and Deb, K. <a href="http://dx.doi.org/10.1201/9781315183176-12">Metaheuristic Techniques.</a> <em>Decision Sciences.</em> CRC Press, pp. 693-750, Nov. 2016.
* Lyazid Toumi, Abdelouahab Moussaoui, and Ahmet Ugur. <a href="http://dx.doi.org/10.1145/2816839.2816876">EMeD-Part: An Efficient Methodology for Horizontal Partitioning in Data Warehouses.</a> <em>Proceedings of the International Conference on Intelligent Information Processing, Security and Advanced Communication.</em> Djallel Eddine Boubiche, Faouzi Hidoussi, and Homero Toral Cruz (Eds.). ACM, New York, NY, USA, Article 43, 7 pages, 2015.
* Andreas Holzinger (Editor), Igo Jurisica (Editor). <a href="http://www.springer.com/computer/database+management+%26+information+retrieval/book/978-3-662-43967-8">Interactive Knowledge Discovery and Data Mining in Biomedical Informatics.</a> <em>Lecture Notes in Computer Science, Vol. 8401.</em> <a href="http://www.springer.com">Springer</a>, 2014.
* Lyazid Toumi, Abdelouahab Moussaoui, Ahmet Ugur. <a href="http://link.springer.com/article/10.1007%2Fs11227-013-1058-9">Particle swarm optimization for bitmap join indexes selection problem in data warehouses.</a> <em><a href="http://link.springer.com/journal/11227">The Journal of Supercomputing</a>, Volume 68, <a href="http://link.springer.com/journal/11227/68/2/page/1">Issue 2</a>, pp 672-708, May 2014.</em>
* TANG Yi (Guangzhou Power Supply Bureau Limited, Guangzhou 511400, China) <a href="http://en.cnki.com.cn/Article_en/CJFDTOTAL-JXKF201210017.htm"> <em>Study on Object-Oriented Reactive Compensation Allocation Optimization Algorithm for Distribution Networks</em></a>, Oct. 2012.
* John M. Linebarger, Richard J. Detry, Robert J. Glass, Walter E. Beyeler, Arlo L. Ames, Patrick D. Finley, S. Louise Maffitt. <a href="http://prod.sandia.gov/techlib/access-control.cgi/2012/121117.pdf"> <em>Complex Adaptive Systems of Systems Engineering Environment Version 1.0.  </em></a> <a href="http://www.sandia.gov/CasosEngineering/">SAND REPORT</a>, Feb. 2012.


## Release notes

### [5.0.0](https://github.com/jenetics/jenetics/releases/tag/v5.0.0)

#### Improvements

* [#534](https://github.com/jenetics/jenetics/issues/534): Generify `Regression` classes so it can be used for regression analysis of arbitrary types.
* [#529](https://github.com/jenetics/jenetics/issues/529): Implementation of Hybridizing PSM and RSM mutation operator (HPRM)
* [#518](https://github.com/jenetics/jenetics/issues/518): Implementation of Symbolic Regression classes. This makes it easier to solve such optimization problems.
* [#515](https://github.com/jenetics/jenetics/issues/515): Rename `Tree.getIndex(Tree)` to `Tree.indexOf(Tree)`.
* [#509](https://github.com/jenetics/jenetics/issues/509): Allow to collect the nth best optimization results.
```java
final ISeq<EvolutionResult<DoubleGene, Double>> best = engine.stream()
    .limit(Limits.bySteadyFitness(50))
    .flatMap(MinMax.toStrictlyIncreasing())
    .collect(ISeq.toISeq(10));
```
* [#504](https://github.com/jenetics/jenetics/issues/504): Rename `Tree.getChild(int)` to `Tree.childAt(int)`.
* [#500](https://github.com/jenetics/jenetics/issues/500): Implementation of Reverse Sequence mutation operator (RSM).
* [#497](https://github.com/jenetics/jenetics/issues/497): Implement Boolean operators for GP.
* [#496](https://github.com/jenetics/jenetics/issues/496): Implement `GT` operator for GP.
* [#493](https://github.com/jenetics/jenetics/issues/493): Add dotty tree formatter
* [#488](https://github.com/jenetics/jenetics/issues/488): Implement new tree formatter `TreeFormatter.LISP`. This allows to create a Lisp string representation from a given `Tree`.
* [#487](https://github.com/jenetics/jenetics/issues/487): Re-implementation of 'MathTreePruneAlterer'. The new implementation uses the newly introduced Tree Rewriting API, implemented in #442.
* [#486](https://github.com/jenetics/jenetics/issues/486): Implement `TreeRewriteAlterer`, based on the new Tree Rewriting API.
* [#485](https://github.com/jenetics/jenetics/issues/485): Cleanup of `MathExpr` class.
* [#484](https://github.com/jenetics/jenetics/issues/484): The `Tree.toString()` now returns a parentheses string.
* [#481](https://github.com/jenetics/jenetics/issues/481): The parentheses tree representation now only escapes "protected" characters.
* [#469](https://github.com/jenetics/jenetics/issues/469): Implementation of additional `Evaluator` factory methods.
* [#465](https://github.com/jenetics/jenetics/issues/465): Remove fitness scaler classes. The fitness scaler doesn't carry its weight.
* [#455](https://github.com/jenetics/jenetics/issues/455): Implementation of `CompletableFutureEvaluator`.
* [#450](https://github.com/jenetics/jenetics/issues/450): Improvement of `FutureEvaluator` class.
* [#449](https://github.com/jenetics/jenetics/issues/449): The `Engine.Builder` constructor is now public and is the most generic way for creating engine builder instances. All other builder factory methods are calling this _primary_ constructor.
* [#447](https://github.com/jenetics/jenetics/issues/447): Remove evolution iterators. The whole evolution is no performed via streams.
* [#442](https://github.com/jenetics/jenetics/issues/442): Introduce Tree Rewriting API, which allows to define own rewrite rules/system. This is very helpful when solving GP related problems.
```java
final TRS<String> trs = TRS.parse(
    "add(0,$x) -> $x",
    "add(S($x),$y) -> S(add($x,$y))",
    "mul(0,$x) -> 0",
    "mul(S($x),$y) -> add(mul($x,$y),$y)"
);

// Converting the input tree into its normal form.
final TreeNode<String> tree = TreeNode.parse("add(S(0),S(mul(S(0),S(S(0)))))");
trs.rewrite(tree);
assert tree.equals(TreeNode.parse("S(S(S(S(0))))"));
```
* [#372](https://github.com/jenetics/jenetics/issues/372): Allow to define the chromosome index an `Alterer` is allowed to change. This allows to define alterers for specific chromosomes in a genotype.
```java
// The genotype prototype, consisting of 4 chromosomes
final Genotype<DoubleGene> gtf = Genotype.of(
    DoubleChromosome.of(0, 1),
    DoubleChromosome.of(1, 2),
    DoubleChromosome.of(2, 3),
    DoubleChromosome.of(3, 4)
);

// Define the GA engine.
final Engine<DoubleGene, Double> engine = Engine
    .builder(gt -> gt.getGene().doubleValue(), gtf)
    .selector(new RouletteWheelSelector<>())
    .alterers(
        // The `Mutator` is used on chromosome with index 0 and 2.
        PartialAlterer.of(new Mutator<DoubleGene, Double>(), 0, 2),
        // The `MeanAlterer` is used on chromosome 3.
        PartialAlterer.of(new MeanAlterer<DoubleGene, Double>(), 3),
        // The `GaussianMutator` is used on all chromosomes.
        new GaussianMutator<>()
    )
    .build();
```
* [#368](https://github.com/jenetics/jenetics/issues/368): Remove deprecated code.
* [#364](https://github.com/jenetics/jenetics/issues/364): Clean implementation of async fitness functions.
* [#342](https://github.com/jenetics/jenetics/issues/342): The `Tree` accessor names are no longer in a Java Bean style: `getChild(int)` -> `childAt(int)`. This corresponds to the `childAtPath(path)` methods.
* [#331](https://github.com/jenetics/jenetics/issues/331): Remove `hashCode` and `equals` method from `Selector` and `Alterer`.
* [#314](https://github.com/jenetics/jenetics/issues/314): Add factory method for `AdaptiveEngine`, which simplifies its creation.
* [#308](https://github.com/jenetics/jenetics/issues/308): General improvement of object serialization.
* [#50](https://github.com/jenetics/jenetics/issues/50): Improve Genotype validation. The new `Constraint` interface, and its implementation `RetryConstraint`, now allows a finer control of the validation and recreation of individuals.


#### Bugs

* [#520](https://github.com/jenetics/jenetics/issues/520): Fix tree-rewriting for `Const` values. This leads to non-matching nodes when trying to simplify the GP tree.
* [#475](https://github.com/jenetics/jenetics/issues/475): Level function returns different results depending on whether the iterator is iterating through a `ProgramGene` or `TreeNode`.
* [#473](https://github.com/jenetics/jenetics/issues/473): `DynamicGenotype` example causes `IllegalArgumentException`.


_[All Release Notes](RELEASE_NOTES.md)_

## License

The library is licensed under the [Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0.html).

	Copyright 2007-2019 Franz Wilhelmstötter

	Licensed under the Apache License, Version 2.0 (the "License");
	you may not use this file except in compliance with the License.
	You may obtain a copy of the License at

	http://www.apache.org/licenses/LICENSE-2.0

	Unless required by applicable law or agreed to in writing, software
	distributed under the License is distributed on an "AS IS" BASIS,
	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
	See the License for the specific language governing permissions and
	limitations under the License.


## Used software

<a href="https://www.jetbrains.com/idea/">![IntelliJ](http://jenetics.io/img/icon_IntelliJIDEA.png)</a>

