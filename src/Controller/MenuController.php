<?php

namespace App\Controller;

use App\Repository\MenuRepository;
use App\Service\FileUploadService;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Attribute\Route;

#[Route('/menus')]
class MenuController extends AbstractController
{
    public function __construct(
        private MenuRepository $menuRepository,
        private FileUploadService $fileUploadService
    ) {}

    #[Route('', name: 'menu_index')]
    public function index(): Response
    {
        $menus = $this->menuRepository->findAll();
        
        return $this->render('menu/index.html.twig', [
            'menus' => $menus
        ]);
    }

    #[Route('/nouveau', name: 'menu_create')]
    public function create(Request $request): Response
    {
        if ($request->isMethod('POST')) {
            $data = [
                'nom' => $request->request->get('nom')
            ];
            
            $imageFile = $request->files->get('image');
            if ($imageFile) {
                $data['image'] = $this->fileUploadService->upload($imageFile);
            }
            
            $this->menuRepository->create($data);
            
            $this->addFlash('success', 'Menu créé avec succès');
            return $this->redirectToRoute('menu_index');
        }
        
        return $this->render('menu/create.html.twig');
    }

    #[Route('/{id}/modifier', name: 'menu_edit')]
    public function edit(int $id, Request $request): Response
    {
        $menu = $this->menuRepository->findById($id);
        
        if (!$menu) {
            $this->addFlash('error', 'Menu introuvable');
            return $this->redirectToRoute('menu_index');
        }
        
        if ($request->isMethod('POST')) {
            $data = [
                'nom' => $request->request->get('nom')
            ];
            
            $imageFile = $request->files->get('image');
            if ($imageFile) {
                $this->fileUploadService->delete($menu['image']);
                $data['image'] = $this->fileUploadService->upload($imageFile);
            }
            
            $this->menuRepository->update($id, $data);
            
            $this->addFlash('success', 'Menu modifié avec succès');
            return $this->redirectToRoute('menu_index');
        }
        
        return $this->render('menu/edit.html.twig', [
            'menu' => $menu
        ]);
    }

    #[Route('/{id}/archiver', name: 'menu_archive', methods: ['POST'])]
    public function archive(int $id): Response
    {
        if ($this->menuRepository->archive($id)) {
            $this->addFlash('success', 'Menu archivé');
        } else {
            $this->addFlash('error', 'Erreur lors de l\'archivage');
        }
        
        return $this->redirectToRoute('menu_index');
    }
}
