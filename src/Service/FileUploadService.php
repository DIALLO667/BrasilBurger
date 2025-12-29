<?php

namespace App\Service;

use Symfony\Component\HttpFoundation\File\UploadedFile;

class FileUploadService
{
    private string $uploadDirectory;

    public function __construct(string $projectDir)
    {
        $this->uploadDirectory = $projectDir . '/public/uploads/produits';
        
        // Créer le dossier s'il n'existe pas
        if (!is_dir($this->uploadDirectory)) {
            mkdir($this->uploadDirectory, 0777, true);
        }
    }

    public function upload(UploadedFile $file): string
    {
        $filename = uniqid() . '.' . $file->guessExtension();
        $file->move($this->uploadDirectory, $filename);
        
        return '/uploads/produits/' . $filename;
    }

    public function delete(?string $imagePath): void
    {
        if ($imagePath) {
            $fullPath = dirname($this->uploadDirectory, 2) . $imagePath;
            if (file_exists($fullPath)) {
                unlink($fullPath);
            }
        }
    }
}
