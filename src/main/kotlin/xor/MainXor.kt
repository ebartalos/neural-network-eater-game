package xor

import ai.Network
import ai.neurons.SigmoidNeuron
import kotlin.math.abs

object MainXOR {
    @JvmStatic
    fun main(args: Array<String>) {
        val learningRate = 0.25
        val acceptedError = 0.1

        val inputVector = ArrayList<Pair<Double, Double>>()
        val outputVector = ArrayList<Double>()

        inputVector.add(Pair(0.0, 0.0))
        outputVector.add(0.0)

        inputVector.add(Pair(1.0, 1.0))
        outputVector.add(0.0)

        inputVector.add(Pair(0.0, 1.0))
        outputVector.add(1.0)

        inputVector.add(Pair(1.0, 0.0))
        outputVector.add(1.0)

        lateinit var trainedNetwork: Network

        // nice infinite loop to avoid other 15 local minimums
        avoidLocalMinimum@ while (true) {
            val network = Network()

            network.addInputLayer(2, true)
            network.addHiddenLayer(SigmoidNeuron::class, 2, true)
            network.addOutputLayer(SigmoidNeuron::class, 1)
            network.createConnections()

            for (epoch in 0..10000) {
                var isTrainingDone = true
                for (index in 0 until inputVector.size) {
                    network.setInputs(arrayListOf(inputVector[index].first, inputVector[index].second))
                    network.propagate()

                    if (abs(outputVector[index] - network.output().first()) > acceptedError) {
                        isTrainingDone = false
                    }

                    network.backpropagate(listOf(outputVector[index]), learningRate)
                }

                if (isTrainingDone) {
                    println("Training done in epoch $epoch")
                    trainedNetwork = network
                    break@avoidLocalMinimum
                }
            }
        }

        for (i in 0 until inputVector.size) {
            trainedNetwork.setInputs(arrayListOf(inputVector[i].first, inputVector[i].second))
            trainedNetwork.propagate()
            println(
                "${inputVector[i].first}, ${inputVector[i].second}, expected output ${outputVector[i]}, real output " + trainedNetwork.output()
                    .first()
                    .toString()
            )
        }
    }
}