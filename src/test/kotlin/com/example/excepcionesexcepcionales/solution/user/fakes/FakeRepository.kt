package com.example.excepcionesexcepcionales.solution.user.fakes

interface FakeRepository<T> {
    val elements: MutableList<T>

    fun resetFake() = elements.clear()
    fun wasPersisted(resource: T): Boolean = elements.contains(resource)

    fun MutableList<T>.saveOrUpdate(value: T) =
        if (elements.contains(value))
            elements[elements.indexOf(value)] = value
        else elements.add(value)
}
