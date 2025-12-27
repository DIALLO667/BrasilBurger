<?php

namespace App\Repository;

use Doctrine\DBAL\Connection;

class ClientRepository
{
    public function __construct(
        private Connection $connection
    ) {}

    /**
     * Récupérer client par ID
     */
    public function findById(int $id): ?array
    {
        $sql = "SELECT * FROM client WHERE id = :id";
        return $this->connection->executeQuery($sql, ['id' => $id])->fetchAssociative() ?: null;
    }

    /**
     * Stats d'un client
     */
    public function getClientStats(int $clientId): array
    {
        $sql = "
            SELECT 
                COUNT(c.id) as nombre_commandes,
                COALESCE(SUM(c.montant_total), 0) as total_depense,
                MAX(c.date_commande) as derniere_commande
            FROM commande c
            WHERE c.client_id = :clientId
        ";
        
        return $this->connection->executeQuery($sql, ['clientId' => $clientId])->fetchAssociative();
    }

    /**
     * Commandes d'un client
     */
    public function getClientCommandes(int $clientId): array
    {
        $sql = "
            SELECT 
                c.*,
                CASE 
                    WHEN p.id IS NOT NULL THEN true 
                    ELSE false 
                END as est_payee
            FROM commande c
            LEFT JOIN paiement p ON p.commande_id = c.id
            WHERE c.client_id = :clientId
            ORDER BY c.date_commande DESC
        ";
        
        return $this->connection->executeQuery($sql, ['clientId' => $clientId])->fetchAllAssociative();
    }

    /**
     * Rechercher clients par nom/téléphone
     */
    public function search(string $query): array
    {
        $sql = "
            SELECT 
                cl.*,
                COUNT(c.id) as nombre_commandes
            FROM client cl
            LEFT JOIN commande c ON c.client_id = cl.id
            WHERE 
                cl.nom ILIKE :query 
                OR cl.prenom ILIKE :query 
                OR cl.telephone ILIKE :query
                OR cl.email ILIKE :query
            GROUP BY cl.id
            ORDER BY cl.nom, cl.prenom
            LIMIT 20
        ";
        
        return $this->connection->executeQuery($sql, [
            'query' => '%' . $query . '%'
        ])->fetchAllAssociative();
    }
}
