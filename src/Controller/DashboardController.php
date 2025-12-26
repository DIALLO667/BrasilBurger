<?php

namespace App\Controller;

use App\Service\StatistiqueService;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Attribute\Route;

class DashboardController extends AbstractController
{
    public function __construct(
        private StatistiqueService $statistiqueService
    ) {}

    #[Route('/', name: 'dashboard')]
    public function index(): Response
    {
        $stats = $this->statistiqueService->getStatistiquesJour();

        return $this->render('dashboard/index.html.twig', [
            'stats' => $stats,
        ]);
    }
}
