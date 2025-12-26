<?php

namespace App\Entity;

use Doctrine\ORM\Mapping as ORM;

#[ORM\Entity]
#[ORM\Table(name: 'ligne_commande')]
class LigneCommande
{
    #[ORM\Id]
    #[ORM\Column(type: 'integer')]
    #[ORM\GeneratedValue]
    private ?int $id = null;

    #[ORM\Column(name: 'commande_id', type: 'integer')]
    private ?int $commandeId = null;

    #[ORM\Column(name: 'burger_id', type: 'integer', nullable: true)]
    private ?int $burgerId = null;

    #[ORM\Column(name: 'menu_id', type: 'integer', nullable: true)]
    private ?int $menuId = null;

    #[ORM\Column(type: 'integer')]
    private ?int $quantite = 1;

    #[ORM\Column(name: 'prix_unitaire', type: 'decimal', precision: 10, scale: 2)]
    private ?string $prixUnitaire = null;

    // Getters & Setters
    public function getId(): ?int { return $this->id; }
    
    public function getCommandeId(): ?int { return $this->commandeId; }
    public function setCommandeId(int $commandeId): self { $this->commandeId = $commandeId; return $this; }
    
    public function getBurgerId(): ?int { return $this->burgerId; }
    public function setBurgerId(?int $burgerId): self { $this->burgerId = $burgerId; return $this; }
    
    public function getMenuId(): ?int { return $this->menuId; }
    public function setMenuId(?int $menuId): self { $this->menuId = $menuId; return $this; }
    
    public function getQuantite(): ?int { return $this->quantite; }
    public function setQuantite(int $quantite): self { $this->quantite = $quantite; return $this; }
    
    public function getPrixUnitaire(): ?string { return $this->prixUnitaire; }
    public function setPrixUnitaire(string $prixUnitaire): self { $this->prixUnitaire = $prixUnitaire; return $this; }
}
