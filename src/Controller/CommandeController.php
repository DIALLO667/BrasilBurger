<?php

namespace App\Controller;

use App\Service\CommandeService;
use App\DTO\Request\FiltreCommandeDTO;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Attribute\Route;

#[Route('/commandes')]
class CommandeController extends AbstractController
{
    public function __construct(
        private CommandeService $commandeService
    ) {}

    #[Route('', name: 'commande_index')]
    public function index(Request $request): Response
    {
        // Créer DTO filtres depuis request
        $filtres = new FiltreCommandeDTO();
        $filtres->etat = $request->query->get('etat');
        $filtres->type = $request->query->get('type');
        
        if ($request->query->get('dateDebut')) {
            $filtres->dateDebut = new \DateTime($request->query->get('dateDebut'));
        }
        
        if ($request->query->get('dateFin')) {
            $filtres->dateFin = new \DateTime($request->query->get('dateFin'));
        }
        
        $filtres->clientNom = $request->query->get('clientNom');
        
        // Récupérer commandes
        $commandes = $this->commandeService->listerCommandes($filtres);
        
        return $this->render('commande/index.html.twig', [
            'commandes' => $commandes,
            'filtres' => $filtres
        ]);
    }

    #[Route('/{id}', name: 'commande_detail', requirements: ['id' => '\d+'])]
    public function detail(int $id): Response
    {
        $commande = $this->commandeService->getDetailCommande($id);
        
        if (!$commande) {
            $this->addFlash('error', 'Commande introuvable');
            return $this->redirectToRoute('commande_index');
        }
        
        return $this->render('commande/detail.html.twig', [
            'commande' => $commande
        ]);
    }

    #[Route('/{id}/terminer', name: 'commande_terminer', methods: ['POST'])]
    public function terminer(int $id): Response
    {
        if ($this->commandeService->changerEtat($id, 'terminee')) {
            $this->addFlash('success', 'Commande terminée avec succès');
        } else {
            $this->addFlash('error', 'Erreur lors du changement d\'état');
        }
        
        return $this->redirectToRoute('commande_detail', ['id' => $id]);
    }

    #[Route('/{id}/annuler', name: 'commande_annuler', methods: ['POST'])]
    public function annuler(int $id): Response
    {
        if ($this->commandeService->annulerCommande($id)) {
            $this->addFlash('success', 'Commande annulée');
        } else {
            $this->addFlash('error', 'Erreur lors de l\'annulation');
        }
        
        return $this->redirectToRoute('commande_detail', ['id' => $id]);
    }
}
