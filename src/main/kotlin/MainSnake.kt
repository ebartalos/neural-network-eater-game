import ai.Network
import ai.algorithms.Genetics
import ai.neurons.Neuron
import ai.neurons.ReLuNeuron
import snake.ConsoleEater
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter


object MainSnake {

    @JvmStatic
    fun main(args: Array<String>) {
        val networks = arrayListOf<Network>()
        val fitness = hashMapOf<Network, Int>()

        createNetworks(networks, fitness)
        train(networks, fitness)

//        testBest()
    }

    private fun train(networks: ArrayList<Network>, fitness: HashMap<Network, Int>) {
        var sortedFitness: Map<Network, Int>
        var bestFitness = 0

        for (generation in 0..Constants.MAX_GENERATIONS) {
            // reset fitness
            fitness.replaceAll { _, _ -> 0 }

            for (network in networks) {
                fitness[network] = playConsole(network)
            }

            sortedFitness = fitness.toList().sortedBy { (_, value) -> value }.toMap()
            val bestNetwork = sortedFitness.keys.last()

            val genetics = Genetics(sortedFitness.keys.reversed())
            genetics.breed(mutate = true, mutationChance = Constants.MUTATION_CHANCE)

            if (fitness[bestNetwork]!! > bestFitness) {
                bestFitness = fitness[bestNetwork]!!
                bestNetwork.saveTrainedNetworkToFile(overwrite = true)
                playConsole(bestNetwork, saveToFile = true)
            }

            val time = DateTimeFormatter
                .ofPattern("HH:mm:ss")
                .withZone(ZoneId.systemDefault())
                .format(Instant.now())
            println("$time Gen $generation best gen fitness ${fitness[bestNetwork]!!} ATH fitness $bestFitness")
        }
    }

    private fun testBest() {
        val network = Network(1)
        network.loadTrainedNetworkFromFile()
        println("Fitness " + playConsole(network, saveToFile = true))
    }

    private fun createNetworks(networks: ArrayList<Network>, fitness: HashMap<Network, Int>) {
        for (networkId in 1..Constants.MAX_NEURAL_NETWORKS) {
            lateinit var network: Network

            if (Constants.LOAD_NETWORK_FILE_ON_START) {
                network = Network(networkId)
                network.loadTrainedNetworkFromFile()
            } else {
                network = createNetwork(networkId)
            }

            networks.add(network)
            fitness[network] = 0
        }
    }

    private fun createNetwork(id: Int): Network {
        val network = Network(id)
        network.addInputLayer(8)
        network.addHiddenLayer(ReLuNeuron::class, 10, true)
//        network.addHiddenLayer(ReLuNeuron::class, 10, true)
//        network.addHiddenLayer(ReLuNeuron::class, 8, true)
        network.addOutputLayer(Neuron::class, 4)
        network.createConnections()

        return network
    }

    private fun playConsole(network: Network, printBoard: Boolean = false, saveToFile: Boolean = false): Int {
        var fitness = 0
        for (i in 1..5) {
            val snake = ConsoleEater()
            fitness += snake.play(network, printBoard, saveToFile)
        }
        return fitness
    }
}