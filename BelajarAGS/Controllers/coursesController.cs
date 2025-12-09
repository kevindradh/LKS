using BelajarAGS.DTOs;
using BelajarAGS.Models;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using System.Drawing;
using System.Security.Claims;

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

        [Authorize]
        [HttpPost("{courseId}/purchase")]
        public IActionResult Purchase(int courseId, PurchaseDTO purchase)
        {
            var userId = User.FindFirstValue(ClaimTypes.NameIdentifier);
            Course course = _context.Courses.FirstOrDefault(f => f.Id == courseId);
            var parsingUserId = int.TryParse(userId, out int convertedId);
            var couponCode = _context.Coupons.FirstOrDefault(f => f.Code == purchase.CouponCode);
            decimal discounApplied = 0;
            decimal pricePaid = 0;

            if (!parsingUserId)
            {
                return UnprocessableEntity(new { Message = "Invalid user ID!" });
            }

            if (course == null)
            {
                return NotFound(new { Message = "Course not found!" });
            }

            if (couponCode != null)
            {
                var quotaCheck = _context.Purchases.Count(f => f.CouponId == couponCode.Id);

                if (quotaCheck >= couponCode.Quota || couponCode.ExpiryDate.Date < DateTime.Now.Date)
                {
                    return UnprocessableEntity(new { Message = "Validation error: coupon code has expired or quota exceeded." });
                }

                discounApplied = couponCode.DiscountPct;
                var discountAmount = course.Price * discounApplied / 100;
                pricePaid = course.Price - discountAmount;
            }
            else
            {
                pricePaid = course.Price;
            }

            Purchase newPurchase = new Purchase
            {
                UserId = convertedId,
                CourseId = courseId,
                PricePaid = pricePaid,
                PaymentMethod = purchase.PaymentMethod,
                CouponId = couponCode?.Id,
                PurchasedAt = DateTime.Now,
            };

            _context.Purchases.Add(newPurchase);
            _context.SaveChanges();

            return Ok(new
            {
                Message = "Course purchased successfully.",
                Data = new
                {
                    PurchaseId = newPurchase.Id,
                    newPurchase.CourseId,
                    UserId = convertedId,
                    PurchaseDate = newPurchase.PurchasedAt,
                    newPurchase.PaymentMethod,
                    OriginalPrice = course.Price,
                    DiscountApplied = discounApplied,
                    PaidAmount = pricePaid
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
