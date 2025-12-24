<?php

namespace App\Entity;

use Doctrine\ORM\Mapping as ORM;

#[ORM\Entity(repositoryClass: 'App\Repository\LivreurRepository')]
#[ORM\Table(name: 'livreur')]
class Livreur
{
    #[ORM\Id]
    #[ORM\Column(type: 'integer')]
    #[ORM\GeneratedValue]
    private ?int $id = null;

    #[ORM\Column(type: 'string', length: 100)]
    private ?string $nom = null;

    #[ORM\Column(type: 'string', length: 100)]
    private ?string $prenom = null;

    #[ORM\Column(type: 'string', length: 20)]
    private ?string $telephone = null;

    #[ORM\Column(name: 'zone_id', type: 'integer', nullable: true)]
    private ?int $zoneId = null;

    #[ORM\Column(type: 'boolean')]
    private bool $archive = false;

    #[ORM\Column(name: 'date_creation', type: 'datetime')]
    private ?\DateTimeInterface $dateCreation = null;

    public function __construct()
    {
        $this->dateCreation = new \DateTime();
    }

    // Getters & Setters
    public function getId(): ?int { return $this->id; }
    
    public function getNom(): ?string { return $this->nom; }
    public function setNom(string $nom): self { $this->nom = $nom; return $this; }
    
    public function getPrenom(): ?string { return $this->prenom; }
    public function setPrenom(string $prenom): self { $this->prenom = $prenom; return $this; }
    
    public function getTelephone(): ?string { return $this->telephone; }
    public function setTelephone(string $telephone): self { $this->telephone = $telephone; return $this; }
    
    public function getZoneId(): ?int { return $this->zoneId; }
    public function setZoneId(?int $zoneId): self { $this->zoneId = $zoneId; return $this; }
    
    public function isArchive(): bool { return $this->archive; }
    public function setArchive(bool $archive): self { $this->archive = $archive; return $this; }
    
    public function getDateCreation(): ?\DateTimeInterface { return $this->dateCreation; }
}
