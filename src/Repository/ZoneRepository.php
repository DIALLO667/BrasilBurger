<?php

namespace App\Repository;

use App\Entity\Zone;
use Doctrine\Bundle\DoctrineBundle\Repository\ServiceEntityRepository;
use Doctrine\Persistence\ManagerRegistry;

class ZoneRepository extends ServiceEntityRepository
{
    public function __construct(ManagerRegistry $registry)
    {
        parent::__construct($registry, Zone::class);
    }

    /**
     * Toutes les zones
     */
    public function findAllZones(): array
    {
        $conn = $this->getEntityManager()->getConnection();
        $sql = "SELECT * FROM zone ORDER BY nom";
        return $conn->executeQuery($sql)->fetchAllAssociative();
    }

    /**
     * Une zone par ID
     */
    public function findOneById(int $id): ?array
    {
        $conn = $this->getEntityManager()->getConnection();
        $sql = "SELECT * FROM zone WHERE id = :id";
        return $conn->executeQuery($sql, ['id' => $id])->fetchAssociative() ?: null;
    }

    /**
     * Créer une zone
     */
    public function create(array $data): int
    {
        $conn = $this->getEntityManager()->getConnection();
        
        $sql = "
            INSERT INTO zone (nom, quartiers, prix_livraison)
            VALUES (:nom, :quartiers, :prixLivraison)
            RETURNING id
        ";
        
        $result = $conn->executeQuery($sql, [
            'nom' => $data['nom'],
            'quartiers' => $data['quartiers'], // JSON string
            'prixLivraison' => $data['prix_livraison']
        ])->fetchAssociative();
        
        return $result['id'];
    }

    /**
     * Modifier une zone
     */
    public function update(int $id, array $data): bool
    {
        $conn = $this->getEntityManager()->getConnection();
        
        $sql = "
            UPDATE zone 
            SET nom = :nom, quartiers = :quartiers, prix_livraison = :prixLivraison
            WHERE id = :id
        ";
        
        return $conn->executeStatement($sql, [
            'id' => $id,
            'nom' => $data['nom'],
            'quartiers' => $data['quartiers'],
            'prixLivraison' => $data['prix_livraison']
        ]) > 0;
    }

    /**
     * Supprimer une zone
     */
    public function delete(int $id): bool
    {
        $conn = $this->getEntityManager()->getConnection();
        $sql = "DELETE FROM zone WHERE id = :id";
        return $conn->executeStatement($sql, ['id' => $id]) > 0;
    }

    /**
     * Regrouper commandes livraison en cours par zone
     */
    public function getCommandesGroupeesParZone(): array
    {
        $conn = $this->getEntityManager()->getConnection();
        
        $sql = "
            SELECT 
                z.*,
                COUNT(c.id) as nombre_commandes,
                l.id as livreur_id,
                l.nom as livreur_nom,
                l.prenom as livreur_prenom
            FROM zone z
            LEFT JOIN commande c ON c.zone_id = z.id 
                AND c.type_recuperation = 'livraison' 
                AND c.etat = 'en_cours'
            LEFT JOIN livreur l ON l.zone_id = z.id AND NOT l.archive
            GROUP BY z.id, l.id, l.nom, l.prenom
            HAVING COUNT(c.id) > 0
            ORDER BY z.nom
        ";
        
        return $conn->executeQuery($sql)->fetchAllAssociative();
    }

    /**
     * Commandes d'une zone spécifique
     */
    public function getCommandesZone(int $zoneId): array
    {
        $conn = $this->getEntityManager()->getConnection();
        
        $sql = "
            SELECT 
                c.id,
                c.date_commande,
                c.montant_total,
                c.adresse_livraison,
                c.etat,
                cl.nom || ' ' || cl.prenom as client_nom,
                cl.telephone as client_telephone
            FROM commande c
            INNER JOIN client cl ON cl.id = c.client_id
            WHERE c.zone_id = :zoneId 
            AND c.type_recuperation = 'livraison'
            AND c.etat = 'en_cours'
            ORDER BY c.date_commande
        ";
        
        return $conn->executeQuery($sql, ['zoneId' => $zoneId])->fetchAllAssociative();
    }

    /**
     * Affecter un livreur à toutes les commandes d'une zone
     */
    public function affecterLivreurZone(int $zoneId, int $livreurId): int
    {
        $conn = $this->getEntityManager()->getConnection();
        
        $sql = "
            UPDATE commande 
            SET livreur_id = :livreurId
            WHERE zone_id = :zoneId 
            AND type_recuperation = 'livraison'
            AND etat = 'en_cours'
            AND livreur_id IS NULL
        ";
        
        return $conn->executeStatement($sql, [
            'zoneId' => $zoneId,
            'livreurId' => $livreurId
        ]);
    }
}
