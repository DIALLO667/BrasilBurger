<?php

namespace App\Entity;

use Doctrine\ORM\Mapping as ORM;

#[ORM\Entity]
#[ORM\Table(name: 'zone')]
class Zone
{
    #[ORM\Id]
    #[ORM\Column(type: 'integer')]
    #[ORM\GeneratedValue]
    private ?int $id = null;

    #[ORM\Column(type: 'string', length: 100)]
    private ?string $nom = null;

    #[ORM\Column(type: 'text')]
    private ?string $quartiers = null; // Stocké comme JSON array

    #[ORM\Column(name: 'prix_livraison', type: 'decimal', precision: 10, scale: 2)]
    private ?string $prixLivraison = null;

    public function getId(): ?int { return $this->id; }
    
    public function getNom(): ?string { return $this->nom; }
    public function setNom(string $nom): self { $this->nom = $nom; return $this; }
    
    public function getQuartiers(): ?string { return $this->quartiers; }
    public function setQuartiers(string $quartiers): self { $this->quartiers = $quartiers; return $this; }
    
    public function getPrixLivraison(): ?string { return $this->prixLivraison; }
    public function setPrixLivraison(string $prixLivraison): self { $this->prixLivraison = $prixLivraison; return $this; }
}
