/*
package de.randombyte.ktskript.scriptengine

import de.randombyte.ktskript.plus
import javax.script.ScriptContext
import javax.script.ScriptEngine
import javax.script.ScriptException
import kotlin.reflect.*
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.jvm.internal.impl.descriptors.CallableDescriptor
import kotlin.reflect.jvm.internal.impl.renderer.DescriptorRenderer

fun ScriptEngine.bind(bindings: Map<String, Any>) {
    val stringBuilder = StringBuilder()
    bindings.forEach { varName, varValue ->
        stringBuilder + "val " + varName + " = bindings[\"" + varName + "\"] as " + varValue.javaClass.simpleName + "\n"
    }
    val preScript = stringBuilder.toString()

    getBindings(ScriptContext.ENGINE_SCOPE).putAll(bindings)

    try {
        eval(preScript)
    } catch (e: ScriptException) {
        throw RuntimeException("Error while binding!", e)
    }
}

fun ScriptEngine.declareFunction(function: KFunction<*>) {
    val internalFunctionName = "${function.name}_func"
    val script = function.run {
        val parameterSignature = parameters.joinToString(separator = ", ") { "${it.name!!}: ${it.toSourceCode()}" }
        val parameterNames = parameters.joinToString(separator = ", ") { it.name!! }
        val functionNTypeString = toFunctionNSourceCode()

        "fun $name($parameterSignature): ${returnType.toSourceCode()} = (bindings[\"$internalFunctionName\"] as $functionNTypeString).invoke($parameterNames)"
    }

    println(script)

    println(Class.forName("kotlin.reflect.jvm.internal.KFunctionImpl").methods.filter { it.name == "getDescriptor" }.joinToString { it.returnType.name })
    val getDescriptorMethod = Class.forName("kotlin.reflect.jvm.internal.KFunctionImpl").getMethod("getDescriptor")

    val invoke = getDescriptorMethod.invoke(function)

    val functionDescriptor = invoke as CallableDescriptor
    (CallableDescriptor::getValueParameters)(functionDescriptor)

    val functionName = if (function.findAnnotation<KeepFullyQualifiedName>() != null) {
        function.toString().substringBefore("(")
    } else {
        "fun ${function.name}"
    }

    val arguments = functionDescriptor.valueParameters.joinToString(separator = ", ", prefix = "(", postfix = ")") {
        // TODO: vararg
        val parameterType = DescriptorRenderer.FQ_NAMES_IN_TYPES::renderType.call(it.type)
        val parameterName = DescriptorRenderer.FQ_NAMES_IN_TYPES::renderName.call(it.name)
        "$parameterName: $parameterType"
    }

    val script2 = "$functionName($arguments) {  }"

    println(script2)

    println(function)

    getBindings(ScriptContext.ENGINE_SCOPE).put(internalFunctionName, function)

    try {
        eval(script)
    } catch (e: ScriptException) {
        throw RuntimeException("Error while declaring function!", e)
    }
}

private fun KParameter.toSourceCode(): String {
    if (kind == KParameter.Kind.EXTENSION_RECEIVER) return toLambdaSourceCode() // todo: not called, why?
    return type.toSourceCode()
}

fun ScriptEngine.declareFunctions(vararg functions: KFunction<*>) = functions.forEach(::declareFunction)

private fun KType?.toSourceCode(): String {
    if (this == null) return "*"

    val baseName = (classifier!! as KClass<*>).qualifiedName!!
    val typeArguments = if (arguments.isNotEmpty()) {
        arguments.joinToString(prefix = "<", separator = ", ", postfix = ">") {
            it.variance.toSourceCode() + it.type.toSourceCode()
        }
    } else ""

    return baseName + typeArguments + (if (isMarkedNullable) "?" else "")
}

private fun KParameter.toLambdaSourceCode(): String {
    when (kind) {
        KParameter.Kind.EXTENSION_RECEIVER -> {
            println((this.type.classifier as KClass<*>).simpleName)
            println((this.type.arguments.joinToString { it.type.toSourceCode() }))
        }
    }

    return "-1"
}

private fun KFunction<*>.toFunctionNSourceCode(): String {
    val functionNParameterTypes = (parameters.map(KParameter::type) + returnType).joinToString(separator = ", ", transform = KType::toSourceCode)
    return "Function${parameters.size}<$functionNParameterTypes>"
}

private fun KVariance?.toSourceCode() = when (this) {
    null -> ""
    KVariance.INVARIANT -> ""
    KVariance.IN -> "in "
    KVariance.OUT -> "out "
}*/
