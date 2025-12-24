<?php

namespace App\Entity;

use Doctrine\ORM\Mapping as ORM;

#[ORM\Entity(repositoryClass: 'App\Repository\CommandeRepository')]
#[ORM\Table(name: 'commande')]
class Commande
{
    #[ORM\Id]
    #[ORM\Column(type: 'integer')]
    #[ORM\GeneratedValue]
    private ?int $id = null;

    #[ORM\Column(name: 'client_id', type: 'integer')]
    private ?int $clientId = null;

    #[ORM\Column(name: 'date_commande', type: 'datetime')]
    private ?\DateTimeInterface $dateCommande = null;

    #[ORM\Column(name: 'montant_total', type: 'decimal', precision: 10, scale: 2)]
    private ?string $montantTotal = null;

    #[ORM\Column(name: 'type_recuperation', type: 'string', length: 50)]
    private ?string $typeRecuperation = null;

    #[ORM\Column(type: 'string', length: 50)]
    private ?string $etat = 'en_cours';

    #[ORM\Column(name: 'adresse_livraison', type: 'text', nullable: true)]
    private ?string $adresseLivraison = null;

    #[ORM\Column(name: 'zone_id', type: 'integer', nullable: true)]
    private ?int $zoneId = null;

    #[ORM\Column(name: 'livreur_id', type: 'integer', nullable: true)]
    private ?int $livreurId = null;

    // Getters & Setters
    public function getId(): ?int { return $this->id; }
    
    public function getClientId(): ?int { return $this->clientId; }
    public function setClientId(int $clientId): self { $this->clientId = $clientId; return $this; }
    
    public function getDateCommande(): ?\DateTimeInterface { return $this->dateCommande; }
    public function setDateCommande(\DateTimeInterface $dateCommande): self { $this->dateCommande = $dateCommande; return $this; }
    
    public function getMontantTotal(): ?string { return $this->montantTotal; }
    public function setMontantTotal(string $montantTotal): self { $this->montantTotal = $montantTotal; return $this; }
    
    public function getTypeRecuperation(): ?string { return $this->typeRecuperation; }
    public function setTypeRecuperation(string $typeRecuperation): self { $this->typeRecuperation = $typeRecuperation; return $this; }
    
    public function getEtat(): ?string { return $this->etat; }
    public function setEtat(string $etat): self { $this->etat = $etat; return $this; }
    
    public function getAdresseLivraison(): ?string { return $this->adresseLivraison; }
    public function setAdresseLivraison(?string $adresseLivraison): self { $this->adresseLivraison = $adresseLivraison; return $this; }
    
    public function getZoneId(): ?int { return $this->zoneId; }
    public function setZoneId(?int $zoneId): self { $this->zoneId = $zoneId; return $this; }
    
    public function getLivreurId(): ?int { return $this->livreurId; }
    public function setLivreurId(?int $livreurId): self { $this->livreurId = $livreurId; return $this; }
}
