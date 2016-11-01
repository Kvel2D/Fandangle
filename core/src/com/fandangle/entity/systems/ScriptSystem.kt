package com.fandangle.entity.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.fandangle.entity.Mappers
import com.fandangle.entity.components.MovementComponent
import com.fandangle.entity.components.PhysicsComponent
import com.fandangle.entity.components.ScriptComponent
import com.fandangle.entity.components.TransformComponent

/*
 * Like KeyboardControlS, buy the inputs are read from the script file decoded into scriptC
 * A loop executes the script line by line, repeating when reaching control flows(gotos, ifs) or when a line is ended
 * A line operating on movementC sets the movement variable at the start of the scripts and turns it off at the end
 */
class ScriptSystem : IteratingSystem {

    constructor() :
    super(Family.all(ScriptComponent::class.java, TransformComponent::class.java, MovementComponent::class.java, PhysicsComponent::class.java).get())

    constructor(priority: Int) :
    super(Family.all(ScriptComponent::class.java, TransformComponent::class.java, MovementComponent::class.java, PhysicsComponent::class.java).get(), priority)

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val scriptC = Mappers.scriptComponent.get(entity)

        var repeat: Boolean
        do {
            repeat = false

            // END OF SCRIPT
            if (scriptC.currentLine >= scriptC.operations.size) {
                val movementC = Mappers.movementComponent.get(entity)
                movementC.left = false
                movementC.right = false
                movementC.up = false
                movementC.down = false
                movementC.jump = false
                entity.remove(ScriptComponent::class.java)
                return
            }

            val operation = scriptC.operations[scriptC.currentLine]
            when (operation[0]) {

            // GOTO
                0 -> {
                    scriptC.currentLine = operation[1] - 1 // goto starts at 1, array's lowest index is 0
                    repeat = true // repeat to execute the line that goto pointed to
                }

            // LEFT
                1 -> {
                    val movementC = Mappers.movementComponent.get(entity)

                    // Start script, timer is set to operand1, which is the duration of the instruction(in frames)
                    if (scriptC.timer == 0) {
                        scriptC.timer = operation[1]
                        movementC.left = true
                    }

                    if (scriptC.timer > 0) {
                        scriptC.timer--
                        // End script
                        if (scriptC.timer == 0) {
                            scriptC.currentLine++
                            movementC.left = false
                            repeat = true
                        }
                    }
                }

            // RIGHT
                2 -> {
                    val movementC = Mappers.movementComponent.get(entity)

                    // Start script
                    if (scriptC.timer == 0) {
                        scriptC.timer = operation[1]
                        movementC.right = true
                    }

                    if (scriptC.timer > 0) {
                        scriptC.timer--
                        // End script
                        if (scriptC.timer == 0) {
                            scriptC.currentLine++
                            movementC.right = false
                            repeat = true
                        }
                    }
                }

            // JUMP
                3 -> {
                    val movementC = Mappers.movementComponent.get(entity)

                    // Start script
                    if (scriptC.timer == 0) {
                        scriptC.timer = operation[1]
                        movementC.jump = true
                    }

                    if (scriptC.timer > 0) {
                        scriptC.timer--
                        // End script
                        if (scriptC.timer == 0) {
                            scriptC.currentLine++
                            movementC.jump = false
                            repeat = true
                        }
                    }
                }

            // JUMPLEFT
                4 -> {
                    val movementC = Mappers.movementComponent.get(entity)

                    // Start script
                    if (scriptC.timer == 0) {
                        scriptC.timer = operation[1]
                        movementC.jump = true
                        movementC.left = true
                    }

                    if (scriptC.timer > 0) {
                        scriptC.timer--
                        // End script
                        if (scriptC.timer == 0) {
                            scriptC.currentLine++
                            movementC.jump = false
                            movementC.left = false
                            repeat = true
                        }
                    }
                }

            // JUMPRIGHT
                5 -> {
                    val movementC = Mappers.movementComponent.get(entity)

                    // Start script
                    if (scriptC.timer == 0) {
                        scriptC.timer = operation[1]
                        movementC.jump = true
                        movementC.right = true
                    }

                    if (scriptC.timer > 0) {
                        scriptC.timer--
                        // End script
                        if (scriptC.timer == 0) {
                            scriptC.currentLine++
                            movementC.jump = false
                            movementC.right = false
                            repeat = true
                        }
                    }
                }

            // SLEEP
                6 -> {
                    if (scriptC.timer == 0) {
                        scriptC.timer = operation[1]
                        val movementC = Mappers.movementComponent.get(entity)
                        movementC.left = false
                        movementC.right = false
                        movementC.up = false
                        movementC.down = false
                        movementC.jump = false
                    }

                    if (scriptC.timer > 0) {
                        scriptC.timer--
                        if (scriptC.timer == 0) {
                            scriptC.currentLine++
                            repeat = true
                        }
                    }
                }

            // LAND
                7 -> {
                    val physicsC = Mappers.physicsComponent.get(entity)
                    val movementC = Mappers.movementComponent.get(entity)

                    if (scriptC.timer == 0) {
                        scriptC.timer = 1
                        movementC.left = false
                        movementC.right = false
                        movementC.up = false
                        movementC.down = true
                        movementC.jump = false
                    }

                    // End if grounded or bounced off an enemy
                    if (physicsC.grounded || movementC.bounceFramesLeft != 0) {
                        scriptC.timer = 0
                        scriptC.currentLine++
                        movementC.down = false
                        repeat = true
                    }
                }

            // IF
                8 -> {
                    val physicsC = Mappers.physicsComponent.get(entity)
                    val transformC = Mappers.transformComponent.get(entity)
                    val variable: Int =
                            when (operation[1]) {
                                0 -> transformC.x.toInt()
                                1 -> transformC.y.toInt()
                                2 -> if (physicsC.grounded) 1 else 0
                                else -> transformC.x.toInt()
                            }
                    val condition = operation[2]
                    val value = operation[3]
                    val result =
                            when (condition) {
                                0 -> variable < value
                                1 -> variable <= value
                                2 -> variable > value
                                3 -> variable >= value
                                4 -> variable == value
                                else -> false
                            }

                    if (result) {
                        scriptC.currentLine++
                        repeat = true
                    } else {
                        scriptC.currentLine += 2
                        repeat = true
                    }
                }

            // UP
                9 -> {
                    val movementC = Mappers.movementComponent.get(entity)

                    // Start script
                    if (scriptC.timer == 0) {
                        scriptC.timer = operation[1]
                        movementC.up = true
                    }

                    if (scriptC.timer > 0) {
                        scriptC.timer--
                        // End script
                        if (scriptC.timer == 0) {
                            scriptC.currentLine++
                            movementC.up = false
                            repeat = true
                        }
                    }
                }

            // DOWN
                10 -> {
                    val movementC = Mappers.movementComponent.get(entity)

                    // Start script
                    if (scriptC.timer == 0) {
                        scriptC.timer = operation[1]
                        movementC.down = true
                    }

                    if (scriptC.timer > 0) {
                        scriptC.timer--
                        // End script
                        if (scriptC.timer == 0) {
                            scriptC.currentLine++
                            movementC.down = false
                            repeat = true
                        }
                    }
                }
            }
        } while (repeat)
    }
}


