In the following assignment i have implemented a work stealing scheduler,
and used it to build a smart phone factory. 
The work stealing principle in this assignment can be synthesized as follows:
Pool of Threads P1, P2, ..., Pn where each thread runs a Processor, each with own queue are working in order to complete
tasks in their queues. 
When a processor Pi is out of tasks it attempts to steal some from its neighbors.

in this project i used a double-ended queue, 
where the owner dequeues from one end and the thief dequeues from another.

in the second part of this project , i implemented a mobile phone factory where diligent workers practice work stealing in order to
quickly assemble products ordered by their clients.
Products and their parts are identified by ID’s . Parts are assembled
according to plans using special tools.

3.1 Plans
A plan provides instructions for assembling a product using tools and parts. The plan consists of a tool list and a
part list, each of these lists can either be empty, contain one item, or contain several items.
In order to complete a plan, a worker must first acquire all products in the part list. This is achieved by spawning
a new manufacture task for each part. Once the last product in the part list is manufactured, the manufacturing
task callback should trigger the part assembly process. Each product should keep a list of its parts.
During the part assembly process, each tool in the tool list must be acquired from the warehouse.
Once the tool is acquired its useOn function is called on each part in the part list, this is repeated for each tool in
the tool list. Finally the return values of all useOn function calls are summed and become the ID of the resulting
product.

3.2 Tools
The interface Tool requires two public functions:
getType() - Which returns a string describing the tool type.
useOn(Product p) - Which returns a long value representing the result of tool use.
Our mobile phone factory will stock the 3 tools in its warehouse:
 RandomSumPliers
 GCD Screwdriver
Next Prime Hammer

3.3 Warehouse
The warehouse class holds a finite amount of each tool.
You can acquire tool and release tool.


3.4 Simulator
The simulator class is tasked with running the simulation. This classes constructor receives only one parameter:
a WorkStealingThreadPool instance. 
Once constructed, calling the simulators start() function will perform the following:
• Read a wave from the input file (JSON format).
• Add a manufacturing task for all products in the current wave to the task queue.
• Proceed to the next wave once all products in the current wave have been manufactured.

Finally, the start() function will return a ConcurrentLinkedQueue containing all
products manufactured during the simulation.
