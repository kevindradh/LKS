using BelajarAGS.Models;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using System.Drawing;

namespace BelajarAGS.Controllers
{
    [Route("gsa-api/v1/[controller]")]
    [ApiController]
    public class coursesController(GsaContext _context) : ControllerBase
    {

        [HttpGet]
        public IActionResult Courses(string? title, string? sort = "DESC", int page = 1, int size = 10)
        {
            if (page < 1)
            {
                return UnprocessableEntity(new { Message = "'page' must be a positive integer." });
            }

            IQueryable<Course> courses = _context.Courses.AsQueryable();

            if (sort == "DESC")
            {
                courses = courses
                    .Where(f => f.Title
                    .Contains(title ?? ""))
                    .OrderByDescending(f => f.CreatedAt)
                    .Skip((page - 1) * size)
                    .Take(size);
            }
            else if (sort == "ASC")
            {
                courses = courses
                    .Where(f => f.Title
                    .Contains(title ?? ""))
                    .OrderBy(f => f.CreatedAt)
                    .Skip((page - 1) * size)
                    .Take(size);
            }
            else
            {
                return UnprocessableEntity(new { Message = "Sort must be ASC or DESC" });
            }

            int dataCount = _context.Courses.Count();

            return Ok(new
            {
                Data = courses,
                Pagination = new
                {
                    Page = page,
                    Size = size,
                    TotalPages = (int)Math.Ceiling(dataCount / (double)size)
                }
            });
        }

        [HttpGet("{courseId}")]
        public IActionResult DetailCourses(int courseId)
        {
            object c = _context
                .Courses
                .AsNoTracking()
                .Where(f => f.Id == courseId)
                .Select(c => new
                {
                    c.Id,
                    c.Title,
                    c.Description,
                    c.Price,
                    c.Duration,
                    Modules = c.Modules.Select(f => f.Title).ToList()
                })
                .FirstOrDefault();

            if (c == null)
            {
                return NotFound(new { Message = "Course not found." });
            }

            return Ok(c);
        }
    }
}
