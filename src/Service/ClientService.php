<?php

namespace App\Service;

use App\Repository\ClientRepository;
use App\DTO\Response\ClientDetailDTO;
use App\DTO\Response\CommandeListDTO;

class ClientService
{
    public function __construct(
        private ClientRepository $clientRepository
    ) {}

    public function getClientDetail(int $clientId): ?ClientDetailDTO
    {
        $client = $this->clientRepository->findById($clientId);
        
        if (!$client) {
            return null;
        }
        
        $stats = $this->clientRepository->getClientStats($clientId);
        
        return new ClientDetailDTO($client, $stats);
    }

    public function getClientCommandes(int $clientId): array
    {
        $commandes = $this->clientRepository->getClientCommandes($clientId);
        
        return array_map(function($c) {
            return [
                'id' => $c['id'],
                'date_commande' => new \DateTime($c['date_commande']),
                'montant_total' => (float) $c['montant_total'],
                'type_recuperation' => $c['type_recuperation'],
                'etat' => $c['etat'],
                'est_payee' => $c['est_payee']
            ];
        }, $commandes);
    }

    public function rechercherClients(string $query): array
    {
        return $this->clientRepository->search($query);
    }
}
