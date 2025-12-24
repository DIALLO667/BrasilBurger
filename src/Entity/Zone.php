<?php

namespace App\Entity;

use Doctrine\ORM\Mapping as ORM;

#[ORM\Entity(repositoryClass: 'App\Repository\ZoneRepository')]
#[ORM\Table(name: 'zone')]
class Zone
{
    #[ORM\Id]
    #[ORM\Column(type: 'integer')]
    #[ORM\GeneratedValue]
    private ?int $id = null;

    #[ORM\Column(type: 'string', length: 100)]
    private ?string $nom = null;

    #[ORM\Column(type: 'text', nullable: true)]
    private ?string $quartiers = null;

    #[ORM\Column(name: 'prix_livraison', type: 'decimal', precision: 10, scale: 2)]
    private ?string $prixLivraison = null;

    // Getters & Setters
    public function getId(): ?int { return $this->id; }
    
    public function getNom(): ?string { return $this->nom; }
    public function setNom(string $nom): self { $this->nom = $nom; return $this; }
    
    public function getQuartiers(): ?string { return $this->quartiers; }
    public function setQuartiers(?string $quartiers): self { $this->quartiers = $quartiers; return $this; }
    
    public function getPrixLivraison(): ?string { return $this->prixLivraison; }
    public function setPrixLivraison(string $prixLivraison): self { $this->prixLivraison = $prixLivraison; return $this; }
}
