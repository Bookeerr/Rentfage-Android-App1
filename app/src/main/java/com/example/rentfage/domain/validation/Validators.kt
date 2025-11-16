package com.example.rentfage.domain.validation

import android.util.Patterns

fun validateEmail(email: String): String? {
    if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
        return "Email no valido"
    }
    return null
}

fun validateNameLettersOnly(name: String): String? {
    if (name.any { !it.isLetter() && !it.isWhitespace() }) {
        return "El nombre solo puede contener letras y espacios"
    }
    return null
}

fun validatePhoneDigitsOnly(phone: String): String? {
    if (phone.any { !it.isDigit() }) {
        return "El telefono solo puede contener digitos"
    }
    if (phone.length < 8) {
        return "El telefono debe tener al menos 8 digitos"
    }
    return null
}

fun validateStrongPassword(password: String): String? {
    if (password.length < 8) {
        return "La contraseña debe tener al menos 8 caracteres"
    }
    if (!password.any { it.isDigit() }) {
        return "La contraseña debe contener al menos un numero"
    }
    if (!password.any { it.isUpperCase() }) {
        return "La contraseña debe contener al menos una mayuscula"
    }
    return null
}

fun validateConfirm(pass: String, confirm: String): String? {
    if (pass != confirm) {
        return "Las contraseñas no coinciden"
    }
    return null
}
