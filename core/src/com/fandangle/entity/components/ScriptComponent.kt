package com.fandangle.entity.components

import com.badlogic.ashley.core.Component

class ScriptComponent : Component {
    val operations: Array<IntArray>
    var timer = 0
    var currentLine = 0

    constructor(operations: Array<IntArray>) {
        this.operations = operations
    }
}
