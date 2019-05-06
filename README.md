# CacheSimTestXML
Author: Magore86

Please read before reading code and running CacheSim.jar! 

The cache simulator can simulate many different scenarios and configurations. At maximum 2 non-shared caches.
The memory usage is rather high, so there is a limit to at max 1023 processors. In addition to this, please be adviced that memory usage can be as high as 1500MB.
Also, please be advised that the simulator is tested on executable file ".exe" less than 800KB. At the moment there is no limit to the size of the input program. But please do not attempt to simulate larger programs. However, if program is too large, the GC will time-out and cause an exception.

Recommended memory requirements:

4-8GB of RAM

Due to the number of objects generated, this program consumes alot of memory.

Also, feedback is welcomed e.g. how to optimize the simulator. It is known that the there may be some objects that could represtented differently.

Also the program implements an .xlsx-writer. This can be found in a different repository called MS Excel writer for Cache Simulator.

Thanks to http://ref.x86asm.net/ and the people behind it, for a great overview of x86 instructions. The x86 instructions are parsed in FileReader.java.


Regards

Magore86
