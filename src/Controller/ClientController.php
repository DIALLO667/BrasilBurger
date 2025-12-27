<?php

namespace App\Controller;

use App\Service\ClientService;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Attribute\Route;

#[Route('/clients')]
class ClientController extends AbstractController
{
    public function __construct(
        private ClientService $clientService
    ) {}

    #[Route('/recherche', name: 'client_recherche')]
    public function recherche(Request $request): Response
    {
        $query = $request->query->get('q', '');
        $clients = [];
        
        if (strlen($query) >= 2) {
            $clients = $this->clientService->rechercherClients($query);
        }
        
        return $this->render('client/recherche.html.twig', [
            'query' => $query,
            'clients' => $clients
        ]);
    }

    #[Route('/{id}', name: 'client_detail', requirements: ['id' => '\d+'])]
    public function detail(int $id): Response
    {
        $client = $this->clientService->getClientDetail($id);
        
        if (!$client) {
            $this->addFlash('error', 'Client introuvable');
            return $this->redirectToRoute('client_recherche');
        }
        
        $commandes = $this->clientService->getClientCommandes($id);
        
        return $this->render('client/detail.html.twig', [
            'client' => $client,
            'commandes' => $commandes
        ]);
    }
}
