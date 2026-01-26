package com.churninsight_dev.backend_api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class LoginDto {
	private String userName;
	private String email;

	@NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 8, max = 15, message = "La contraseña debe tener entre 8 y 15 caracteres.")
    @Pattern(
        // Explicación del Regex:
        // (?=.*[0-9])       -> Al menos un número
        // (?=.*[a-z])       -> Al menos una minúscula
        // (?=.*[A-Z])       -> Al menos una mayúscula
        // (?=.*[@#$!&\\-])  -> Al menos un carácter especial de la lista (@, #, $, !, -, &)
        // .                 -> Cualquier carácter (combinado con el Size arriba)
        regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$!&\\-]).*$",
        message = "Debe incluir al menos una mayúscula, una minúscula, un número y un carácter especial (@, #, $, !, -, &)."
    )
	private String password;

	public LoginDto() {
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String username) {
		this.userName = username;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}


