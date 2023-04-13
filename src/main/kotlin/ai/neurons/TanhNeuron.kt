package ai.neurons

import kotlin.math.tanh

class TanhNeuron : Neuron() {

    override fun activation(vector: Double): Double {
        return tanh(vector)
    }

    override fun derivative(value: Double): Double {
        return 1 - (value * value)
    }
}