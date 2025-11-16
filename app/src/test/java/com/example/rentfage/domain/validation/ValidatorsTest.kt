package com.example.rentfage.domain.validation

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

// Tests para las funciones de validacion, usando Robolectric.
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ValidatorsTest {

    // --- Tests para validateEmail ---

    // Verifica que un email con formato correcto es valido.
    @Test
    fun `validateEmail con email valido no devuelve error`() {
        val resultado = validateEmail("test@example.com")
        assertNull("El email valido no deberia devolver error", resultado)
    }

    // Verifica que un email con formato incorrecto devuelve error.
    @Test
    fun `validateEmail con email invalido devuelve error`() {
        val resultado = validateEmail("email-invalido")
        assertEquals("Email no valido", resultado)
    }

    // --- Tests para validateStrongPassword ---

    // Verifica que una contraseña que cumple todas las reglas es valida.
    @Test
    fun `validateStrongPassword con contraseña valida no devuelve error`() {
        val passwordValida = "Password123"
        val resultado = validateStrongPassword(passwordValida)
        assertEquals(null, resultado)
    }

    // Verifica que una contraseña demasiado corta es rechazada.
    @Test
    fun `validateStrongPassword con contraseña corta devuelve error de longitud`() {
        val passwordCorta = "Pass1"
        val mensajeEsperado = "La contraseña debe tener al menos 8 caracteres"
        val resultado = validateStrongPassword(passwordCorta)
        assertEquals(mensajeEsperado, resultado)
    }

    // Verifica que una contraseña sin ningun numero es rechazada.
    @Test
    fun `validateStrongPassword sin numero devuelve error de numero`() {
        val passwordSinNumero = "PasswordSinNumeros"
        val mensajeEsperado = "La contraseña debe contener al menos un numero"
        val resultado = validateStrongPassword(passwordSinNumero)
        assertEquals(mensajeEsperado, resultado)
    }

    // Verifica que una contraseña sin mayusculas es rechazada.
    @Test
    fun `validateStrongPassword sin mayuscula devuelve error de mayuscula`() {
        val passwordSinMayuscula = "password123"
        val mensajeEsperado = "La contraseña debe contener al menos una mayuscula"
        val resultado = validateStrongPassword(passwordSinMayuscula)
        assertEquals(mensajeEsperado, resultado)
    }
}
