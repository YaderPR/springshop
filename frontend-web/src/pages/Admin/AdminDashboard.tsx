import React, { useState, useEffect } from "react";
import { useKeycloak } from "@react-keycloak/web";
import { getOrders } from "../../services/order/OderService";
import { getUsers } from "../../services/user/UserService";
import type { OrderResponseDto } from "../../types/Order.types";
import type { UserResponse } from "../../types/User.types";
import {
  Loader2,
  AlertTriangle,
  Users,
  ShoppingBag,
  DollarSign,
  CheckCircle,
  CircleAlert,
  PackageSearch,
  LayoutDashboard,
  FileText,
} from "lucide-react";
import { Link } from "react-router-dom";
import InventoryReport from "../../components/Admin/IntentoryReport";
import ProductReport from "../../components/Admin/ProductReport";

const StatCard = ({
  title,
  value,
  icon,
  colorClass,
}: {
  title: string;
  value: string | number;
  icon: React.ReactNode;
  colorClass: string;
}) => (
  <div
    className={`bg-primary border ${colorClass} rounded-lg shadow-md p-6 flex items-center gap-4`}
  >
    <div className={`p-3 rounded-full ${colorClass} bg-opacity-20`}>{icon}</div>
    <div>
      <p className="text-sm font-medium text-gray-400">{title}</p>
      <p className="text-3xl font-bold text-white">{value}</p>
    </div>
  </div>
);

const DashboardSummary = () => {
  const { keycloak, initialized } = useKeycloak();
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const [totalSales, setTotalSales] = useState(0);
  const [totalOrders, setTotalOrders] = useState(0);
  const [totalUsers, setTotalUsers] = useState(0);
  const [recentOrders, setRecentOrders] = useState<OrderResponseDto[]>([]);

  useEffect(() => {
    if (!initialized || !keycloak.token) {
      if (initialized) setError("No se encontró token.");
      setLoading(false);
      return;
    }

    const loadDashboardData = async () => {
      setLoading(true);
      setError(null);
      try {
        const [ordersData, usersData] = await Promise.all([
          getOrders(keycloak.token!),
          getUsers(keycloak.token!),
        ]);

        const sales = ordersData.reduce(
          (sum, order) =>
            order.status === "PAID" ? sum + order.totalAmount : sum,
          0
        );
        setTotalSales(sales);
        setTotalOrders(ordersData.length);

        const sortedOrders = [...ordersData].sort(
          (a, b) =>
            new Date(b.createAt).getTime() - new Date(a.createAt).getTime()
        );
        setRecentOrders(sortedOrders.slice(0, 5));
        setTotalUsers(usersData.length);
      } catch (err: any) {
        setError(err.message || "Un error desconocido ocurrió.");
      } finally {
        setLoading(false);
      }
    };

    loadDashboardData();
  }, [initialized, keycloak.token]);

  if (loading) {
    return (
      <div className="flex items-center justify-center h-64">
        <Loader2 className="w-12 h-12 animate-spin text-secondary" />
      </div>
    );
  }

  if (error) {
    return (
      <div className="flex items-center justify-center h-64 bg-red-900/20 border border-red-700 p-6 rounded-lg">
        <AlertTriangle className="w-12 h-12 text-red-500 mr-4" />
        <div>
          <h2 className="text-xl font-bold text-red-400">
            Error al Cargar Resumen
          </h2>
          <p className="text-red-300">{error}</p>
        </div>
      </div>
    );
  }

  return (
    <div className="space-y-8">
      <h1 className="text-3xl font-bold text-secondary">Dashboard</h1>

      {/* --- 1. Tarjetas de Estadísticas --- */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
        <StatCard
          title="Ventas Totales (Pagadas)"
          value={`$${totalSales.toFixed(2)}`}
          icon={<DollarSign className="text-green-400" />}
          colorClass="border-green-700"
        />
        <StatCard
          title="Órdenes Totales"
          value={totalOrders}
          icon={<ShoppingBag className="text-blue-400" />}
          colorClass="border-blue-700"
        />
        <StatCard
          title="Usuarios Totales"
          value={totalUsers}
          icon={<Users className="text-purple-400" />}
          colorClass="border-purple-700"
        />
      </div>

      {/* --- 2. Lista de Órdenes Recientes --- */}
      <div>
        <h2 className="text-2xl font-bold text-white mb-4">
          Órdenes Recientes
        </h2>
        <div className="bg-primary border border-gray-700 rounded-lg shadow-md">
          <div className="space-y-0">
            {recentOrders.length > 0 ? (
              recentOrders.map((order, index) => (
                <div
                  key={order.id}
                  className={`flex flex-col sm:flex-row sm:justify-between sm:items-center p-4 gap-2
                  ${index < recentOrders.length - 1 ? "border-b border-gray-700" : ""}`}
                >
                  <div className="space-y-1">
                    <span className="text-lg font-bold text-white">
                      Orden #{order.id}
                    </span>
                    <div className="text-sm text-gray-400">
                      <span>Usuario ID: {order.userId}</span>
                      <span className="mx-2">|</span>
                      <span>{new Date(order.createAt).toLocaleString()}</span>
                    </div>
                  </div>

                  <div className="flex items-center gap-4">
                    <span className="text-xl font-bold text-secondary w-28 text-left sm:text-right">
                      ${order.totalAmount.toFixed(2)}
                    </span>
                    {order.status === "PAID" ? (
                      <span className="flex items-center gap-1 text-xs font-bold px-2 py-0.5 rounded-full bg-secondary/20 text-secondary w-28 justify-center">
                        <CheckCircle size={14} /> PAGADO
                      </span>
                    ) : (
                      <span className="flex items-center gap-1 text-xs font-bold px-2 py-0.5 rounded-full bg-yellow-500/20 text-yellow-400 w-28 justify-center">
                        <CircleAlert size={14} /> {order.status}
                      </span>
                    )}
                  </div>
                </div>
              ))
            ) : (
              <p className="text-center p-8 text-gray-500">
                No se encontraron órdenes recientes.
              </p>
            )}
          </div>
          {totalOrders > 5 && (
            <Link
              to="/admin/orders"
              className="block w-full text-center p-3 bg-gray-800 hover:bg-gray-700 rounded-b-lg font-medium text-secondary"
            >
              Ver todas las {totalOrders} órdenes
            </Link>
          )}
        </div>
      </div>
    </div>
  );
};

// --- 3. El componente Dashboard principal (con Pestañas) ---
type DashboardTab = "summary" | "inventory" | "products";

export default function AdminDashboard() {
  const [activeTab, setActiveTab] = useState<DashboardTab>('summary');

  const getTabClass = (tabName: DashboardTab) => {
    return `flex items-center gap-2 py-3 px-5 font-semibold transition-colors
      ${
        activeTab === tabName
          ? 'border-b-2 border-secondary text-secondary'
          : 'text-gray-400 hover:text-white border-b-2 border-transparent'
      }`;
  };

  return (
    <div className="space-y-6">
      <h1 className="text-3xl font-bold text-secondary">Dashboard</h1>

      {/* Contenedor de Pestañas */}
      <div className="flex border-b border-gray-700">
        <button
          onClick={() => setActiveTab('summary')}
          className={getTabClass('summary')}
        >
          <LayoutDashboard size={18} />
          Resumen
        </button>
        <button
          onClick={() => setActiveTab('inventory')}
          className={getTabClass('inventory')}
        >
          <PackageSearch size={18} />
          Reporte de bajo Stock
        </button>
        {/* --- 3. Añadimos la nueva pestaña --- */}
        <button
          onClick={() => setActiveTab('products')}
          className={getTabClass('products')}
        >
          <FileText size={18} />
          Reporte de Productos
        </button>
      </div>

      {/* Contenido de Pestañas (Renderizado Condicional) */}
      <div>
        {activeTab === 'summary' && <DashboardSummary />}
        {activeTab === 'inventory' && <InventoryReport />}
        {activeTab === 'products' && <ProductReport />}
      </div>
    </div>
  );
}
