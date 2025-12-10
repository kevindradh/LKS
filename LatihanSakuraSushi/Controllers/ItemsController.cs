using LatihanSakuraSushi.Models;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;

namespace LatihanSakuraSushi.Controllers
{
    [Route("api/[controller]")]
    [ApiController]
    public class ItemsController(SakuraSushiContext _context) : ControllerBase
    {
        [HttpGet]
        public IActionResult Items(string? search)
        {
            var categories = _context.Categories
                .Include(f => f.Items)
                .Select(f => new
                {
                    f.Name,
                    f.Description,
                    Items = f.Items.Where(f => f.Name.Contains(search ?? "") || f.Description.Contains(search ?? "")).Select(f => new
                    {
                        f.Id,
                        f.Name,
                        f.Description,
                        f.Price,
                        f.Available
                    }).ToList()
                }).Where(f => f.Items.Count > 1).ToList();

            return Ok(categories);
        }
    }
}
