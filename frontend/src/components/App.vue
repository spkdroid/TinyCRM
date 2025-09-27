
/* Responsive Design Enhancements */
@media (max-width: 1024px) {
  .app-main {
    padding: 24px;
  }
  
  .sidebar-menu :deep(.el-menu-item) {
    margin: 4px 8px;
  }
}

@media (max-width: 768px) {
  .app-header {
    padding: 0 16px;
  }
  
  .app-title {
    display: none;
  }
  
  .logo {
    height: 40px;
    margin-right: 12px;
  }
  
  .user-name {
    display: none;
  }
  
  .app-main {
    padding: 16px;
  }
  
  .sidebar-menu {
    padding: 8px 0;
  }
  
  .sidebar-menu :deep(.el-menu-item) {
    margin: 2px 8px;
    height: 44px;
    line-height: 44px;
    font-size: 14px;
  }
  
  .sidebar-menu :deep(.el-menu-item span) {
    display: none;
  }
  
  .app-sidebar {
    width: 80px !important;
  }
}

@media (max-width: 640px) {
  .header-left {
    flex: 1;
  }
  
  .header-right {
    gap: 8px;
  }
  
  .app-main {
    padding: 12px;
  }
}

/* Dark mode support (future enhancement) */
@media (prefers-color-scheme: dark) {
  :root {
    --bg-color: #1a202c;
    --text-color: #e2e8f0;
    --border-color: #2d3748;
  }
}

/* Print styles */
@media print {
  .app-header,
  .app-sidebar {
    display: none;
  }
  
  .app-main {
    margin: 0;
    padding: 0;
    background: white;
  }
}