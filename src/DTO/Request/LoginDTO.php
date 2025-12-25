<?php

namespace App\DTO\Request;

use Symfony\Component\Validator\Constraints as Assert;

class LoginDTO
{
    #[Assert\NotBlank(message: "L'email est requis")]
    #[Assert\Email(message: "Email invalide")]
    public ?string $email = null;

    #[Assert\NotBlank(message: "Le mot de passe est requis")]
    public ?string $password = null;
}
